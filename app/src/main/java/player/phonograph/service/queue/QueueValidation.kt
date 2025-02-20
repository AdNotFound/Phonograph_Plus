/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.service.queue

import player.phonograph.R
import player.phonograph.model.Song
import player.phonograph.repo.loader.Songs
import android.content.Context

fun validSongs(context: Context, songs: List<Song>): List<Song> {
    return songs.map { song ->
        findSong(context, song) ?: markDeleted(context, song)
    }
}

private fun findSong(context: Context, song: Song): Song? =
    Songs.id(context, song.id).takeIf { Song.EMPTY_SONG != it }
        ?: Songs.path(context, song.data).takeIf { Song.EMPTY_SONG != it }

private fun markDeleted(context: Context, song: Song): Song {
    val prefix = "[${context.getString(R.string.deleted)}]"
    val title = song.title
    val new = title.takeIf { title.startsWith(prefix) } ?: (prefix + title)
    return song.copy(title = new)
}