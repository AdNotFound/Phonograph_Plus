/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.mechanism.lyrics

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.logging.ErrorMessage
import org.jaudiotagger.tag.FieldKey
import player.phonograph.App
import player.phonograph.model.Song
import player.phonograph.model.lyrics.AbsLyrics
import player.phonograph.model.lyrics.LrcLyrics
import player.phonograph.model.lyrics.LyricsInfo
import player.phonograph.model.lyrics.LyricsSource
import player.phonograph.model.lyrics.TextLyrics
import player.phonograph.settings.Keys
import player.phonograph.settings.Setting
import player.phonograph.util.FileUtil
import player.phonograph.util.debug
import player.phonograph.util.permissions.hasStorageReadPermission
import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File

object LyricsLoader {

    private val backgroundCoroutine: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    suspend fun loadLyrics(songFile: File, song: Song): LyricsInfo {
        val preference = Setting(App.instance)[Keys.enableLyrics]
        if (!preference.data) {
            debug {
                Log.v(TAG, "Lyrics is off for ${song.title}")
            }
            return LyricsInfo.EMPTY
        }

        if (!hasStorageReadPermission(App.instance)) {
            debug {
                Log.v(TAG, "No storage read permission to fetch lyrics for ${song.title}")
            }
            return LyricsInfo.EMPTY
        }

        // embedded
        val embedded = backgroundCoroutine.async(Dispatchers.IO) {
            parseEmbedded(songFile, LyricsSource.Embedded())
        }

        // external
        val externalPrecise = backgroundCoroutine.async(Dispatchers.IO) {
            val files = getExternalPreciseLyricsFile(songFile)
            files.mapNotNull { parseExternal(it, LyricsSource.ExternalPrecise()) }
        }
        val external = backgroundCoroutine.async(Dispatchers.IO) {
            val files = searchExternalVagueLyricsFiles(songFile, song)
            files.mapNotNull { parseExternal(it, LyricsSource.ExternalDecorated()) }
        }

        val resultList: ArrayList<AbsLyrics> = ArrayList(4)
        resultList.apply {
            val embeddedLyrics = embedded.await()
            if (embeddedLyrics != null) {
                add(embeddedLyrics)
            }
            val preciseLyrics = externalPrecise.await()
            addAll(preciseLyrics)
            val vagueLyrics = external.await()
            addAll(vagueLyrics)
        }

        val activated: Int = resultList.indexOfFirst { it is LrcLyrics }

        // end of fetching
        return LyricsInfo(song, resultList, activated)
    }

    private fun parseEmbedded(
        songFile: File,
        lyricsSource: LyricsSource = LyricsSource.Embedded(),
    ): AbsLyrics? = tryLoad(songFile) {
        AudioFileIO.read(songFile).tag?.getFirst(FieldKey.LYRICS).let { str ->
            if (str != null && str.trim().isNotBlank()) {
                parse(str, lyricsSource)
            } else {
                null
            }
        }
    }

    private fun parseExternal(
        file: File,
        lyricsSource: LyricsSource = LyricsSource.Unknown(),
    ): AbsLyrics? = tryLoad(file) {
        if (file.exists()) {
            val content = file.readText()
            if (content.isNotEmpty()) parse(content, lyricsSource) else null
        } else {
            null
        }
    }

    private fun tryLoad(songFile: File, block: () -> AbsLyrics?): AbsLyrics? =
        try {
            block()
        } catch (e: CannotReadException) {
            val errorMsg = errorMsg(songFile.path, e)
            val suffix = songFile.name.substringAfterLast('.', "")
            if (ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(suffix) == e.message) {
                // ignore
            } else {
                Log.i(TAG, errorMsg)
            }
            TextLyrics.from(errorMsg)
        } catch (e: Exception) {
            val errorMsg = errorMsg(songFile.path, e)
            Log.i(TAG, errorMsg)
            TextLyrics.from(errorMsg)
        }

    private fun errorMsg(path: String, t: Throwable?) =
        "$ERR_MSG_HEADER $path: ${t?.message}\n${Log.getStackTraceString(t)}"


    private fun parse(raw: String, lyricsSource: LyricsSource = LyricsSource.Unknown()): AbsLyrics {
        val lines = raw.take(80).lines()
        val regex = Regex("""(\[.+])+.*""")

        for (line in lines) {
            if (regex.matches(line)) {
                return LrcLyrics.from(raw, lyricsSource)
            }
        }
        return TextLyrics.from(raw, lyricsSource)
    }

    fun getExternalPreciseLyricsFile(songFile: File): List<File> {
        val filename = FileUtil.stripExtension(songFile.absolutePath)
        val lrc = File("$filename.lrc").takeIf { it.exists() }
        val txt = File("$filename.txt").takeIf { it.exists() }
        return listOfNotNull(lrc, txt)
    }

    fun searchExternalVagueLyricsFiles(songFile: File, song: Song): List<File> {
        val dir = songFile.absoluteFile.parentFile ?: return emptyList()

        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val fileName = FileUtil.stripExtension(songFile.name)
        val eFileName = Regex.escape(fileName)
        val eSongName = Regex.escape(song.title)

        // vague pattern
        val vagueRegex =
            Regex(""".*[-;]?($eFileName|$eSongName)[-;]?.*\.(lrc|txt)""", RegexOption.IGNORE_CASE)

        // start list file under the same dir
        val files = try {
            val list = dir.list() ?: return emptyList()
            list.filter { name ->
                when {
                    "$fileName.lrc" == name  -> false
                    "$fileName.txt" == name  -> false
                    vagueRegex.matches(name) -> true
                    else                     -> false
                }
            }.map { File(dir, it) }
        } catch (e: Exception) {
            Log.v(TAG, "Failed to list files: ${e.message}")
            emptyList()
        }

        return if (files.isNotEmpty()) {
            debug {
                Log.v(TAG, files.fold("All lyrics found:") { acc, str -> "${str.path};$acc" })
            }
            files
        } else {
            emptyList()
        }
    }


    fun parseFromUri(context: Context, uri: Uri): AbsLyrics? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.reader().use {
                parse(it.readText(), LyricsSource.ManuallyLoaded())
            }
        }
    }


    private const val TAG = "LyricsLoader"
    private const val ERR_MSG_HEADER = "Failed to read "
}
