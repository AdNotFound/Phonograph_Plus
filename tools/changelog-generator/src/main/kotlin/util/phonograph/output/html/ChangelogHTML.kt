/*
 *  Copyright (c) 2022~2023 chr_56
 */

package util.phonograph.output.html

import util.phonograph.releasenote.Language
import util.phonograph.releasenote.ReleaseChannel
import util.phonograph.releasenote.ReleaseNote
import java.io.File

private const val TAG_LATEST = "<<<LATEST/>>>"
private const val TAG_PREVIEW_START = "<<<PREVIEW>>>"
private const val TAG_PREVIEW_END = "<<</PREVIEW>>>"
private const val TAG_CURRENT_PREVIEW_START = "<<<CURRENT_PREVIEW>>>"
private const val TAG_CURRENT_PREVIEW_END = "<<</CURRENT_PREVIEW>>>"
private const val ANCHOR_LATEST = "<!-- $TAG_LATEST -->"
private const val ANCHOR_PREVIEW_START = "<!-- $TAG_PREVIEW_START -->"
private const val ANCHOR_PREVIEW_END = "<!-- $TAG_PREVIEW_END -->"
private const val ANCHOR_CURRENT_PREVIEW_START = "<!-- $TAG_CURRENT_PREVIEW_START -->"
private const val ANCHOR_CURRENT_PREVIEW_END = "<!-- $TAG_CURRENT_PREVIEW_END -->"
private const val FILE_CHANGELOG_DEFAULT = "changelog.html"
private const val FILE_CHANGELOG_ZH = "changelog-ZH-CN.html"

class ChangelogHTML(
    val lines: MutableList<String>,
) {
    var indexPreviewStart: Int = -1
    var indexPreviewEnd: Int = -1
    var indexCurrentPreviewStart: Int = -1
    var indexCurrentPreviewEnd: Int = -1
    var indexLatest: Int = -1

    /**
     * remove lines between [from] and [to].
     * @param from start index (included)
     * @param to end index (included)
     */
    private fun removeLines(from: Int, to: Int) {
        require(from <= to)
        when {
            from < 0 -> lines.subList(to + 1, lines.size + 1)
            to < 0   -> lines.subList(0, from)
            else     -> {
                // normal
                val count = to - from + 1
                repeat(count) {
                    lines.removeAt(from)
                }
            }
        }
        updateIndexes()
    }

    fun updateIndexes() {
        indexPreviewStart = lines.indexOfFirst { ANCHOR_PREVIEW_START == it }
        indexPreviewEnd = lines.indexOfFirst { ANCHOR_PREVIEW_END == it }
        indexCurrentPreviewStart = lines.indexOfFirst { ANCHOR_CURRENT_PREVIEW_START == it }
        indexCurrentPreviewEnd = lines.indexOfFirst { ANCHOR_CURRENT_PREVIEW_END == it }
        indexLatest = lines.indexOfLast { ANCHOR_LATEST == it }
    }

    fun insertLatestChangelog(newChangelogSection: List<String>): Boolean {
        if (indexLatest < 0) {
            println("No ANCHOR_LATEST")
            return false
        }
        lines.add(indexLatest + 1, "")
        return lines.addAll(indexLatest + 1, newChangelogSection).also { updateIndexes() }
    }

    fun clearPreviewChangelog(): Boolean {
        if (indexPreviewStart < 0 && indexPreviewEnd < 0) {
            println("No ANCHOR_PREVIEW")
            return false
        }
        if (indexPreviewEnd - indexPreviewStart > 1) {
            removeLines(indexPreviewStart + 1, indexPreviewEnd - 1)
        }
        updateIndexes()
        return true
    }

    fun insertPreviewChangelog(newChangelogSection: List<String>): Boolean {
        if (indexPreviewStart < 0 && indexPreviewEnd < 0) {
            println("No ANCHOR_PREVIEW")
            return false
        }

        if (indexCurrentPreviewStart >= 0) {
            lines.removeAt(indexCurrentPreviewStart)
        }
        if (indexCurrentPreviewEnd >= 0) {
            lines.removeAt(indexCurrentPreviewEnd - 1)
        }

        updateIndexes()

        lines.add(indexPreviewStart + 1, ANCHOR_CURRENT_PREVIEW_START)
        lines.add(indexPreviewStart + 2, ANCHOR_CURRENT_PREVIEW_END)
        lines.addAll(indexPreviewStart + 2, newChangelogSection)

        updateIndexes()
        return true
    }

    fun replaceCurrentPreviewChangelog(newChangelogSection: List<String>): Boolean {
        if (indexCurrentPreviewStart < 0 && indexCurrentPreviewEnd < 0) {
            println("No ANCHOR_CURRENT_PREVIEW")
            return false
        }

        removeLines(indexCurrentPreviewStart + 1, indexCurrentPreviewEnd - 1)
        lines.addAll(indexCurrentPreviewStart + 1, newChangelogSection)

        updateIndexes()
        return true
    }

    fun replacePreviewChangelog(newChangelogSection: List<String>): Boolean {
        if (indexPreviewStart < 0 && indexPreviewEnd < 0) {
            println("No ANCHOR_PREVIEW")
            return false
        }

        removeLines(indexPreviewStart + 1, indexPreviewEnd - 1)
        lines.addAll(indexPreviewStart + 1, newChangelogSection)

        updateIndexes()
        return true
    }

    /**
     * output full changelog using LF(`\n`)
     */
    fun output(): String = lines.joinToString(separator = "\n")

    companion object {
        fun parse(fullChangelog: String): ChangelogHTML {
            val lines: Sequence<String> = fullChangelog.lineSequence()
            return ChangelogHTML(lines.toMutableList()).also { it.updateIndexes() }
        }
    }
}

fun updateStableChangelog(changelogsDir: File, lang: Language, releaseNote: String) {
    updateChangelog(changelogFile(changelogsDir, lang)) { changelogHTML ->
        changelogHTML.clearPreviewChangelog()
        changelogHTML.insertLatestChangelog(releaseNote.lines())
    }
}

fun updatePreviewChangelog(changelogsDir: File, lang: Language, releaseNote: String) {
    updateChangelog(changelogFile(changelogsDir, lang)) { changelogHTML ->
        changelogHTML.insertPreviewChangelog(releaseNote.lines())
    }
}

private inline fun updateChangelog(file: File, block: (ChangelogHTML) -> Unit) {
    require(file.exists() && file.isFile)
    val fullChangelog = file.readText()
    val html = ChangelogHTML.parse(fullChangelog)
    block(html)
    file.outputStream().use { fileOutputStream ->
        fileOutputStream.writer().use {
            it.write(html.output())
            it.flush()
        }
    }
}

private fun changelogFile(changelogsDir: File, lang: Language): File =
    when (lang) {
        Language.EN -> File(changelogsDir, FILE_CHANGELOG_DEFAULT)
        Language.ZH -> File(changelogsDir, FILE_CHANGELOG_ZH)
    }

fun updateChangelogs(model: ReleaseNote, changelogsDir: File) {
    require(changelogsDir.exists() && changelogsDir.isDirectory)

    val notes = generateHTML(model)

    for ((lang, note) in notes) {
        when (model.channel) {
            ReleaseChannel.PREVIEW -> updatePreviewChangelog(changelogsDir, lang, note)
            ReleaseChannel.STABLE  -> updateStableChangelog(changelogsDir, lang, note)
            ReleaseChannel.LTS     -> updateStableChangelog(changelogsDir, lang, note)
        }
    }

}