/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.ui.activities

import player.phonograph.model.Song
import player.phonograph.model.getReadableDurationString
import player.phonograph.ui.adapter.DisplayConfig
import player.phonograph.ui.fragments.pages.adapter.SongDisplayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup

class AlbumSongDisplayAdapter(
    activity: AppCompatActivity,
    layoutRes: Int,
    displayConfig: DisplayConfig
) : SongDisplayAdapter(activity, layoutRes, displayConfig, useImageText = false) {

    override fun getSectionNameImp(position: Int): String = getTrackNumber(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayViewHolder<Song> {
        return AlbumSongViewHolder(inflatedView(layoutRes, parent))
    }

    open class AlbumSongViewHolder(itemView: View) : DisplayViewHolder<Song>(itemView) {
        override fun getRelativeOrdinalText(item: Song): String {
            return getTrackNumber(item)
        }

        override fun getDescription(item: Song): CharSequence? {
            return "${getReadableDurationString(item.duration)} · ${item.artistName}"
        }
    }

    companion object {
        private fun getTrackNumber(item: Song): String {
            val num = toFixedTrackNumber(item.trackNumber)
            return if (num > 0) num.toString() else "-"
        }

        // iTunes uses for example 1002 for track 2 CD1 or 3011 for track 11 CD3.
        // this method converts those values to normal track numbers
        private fun toFixedTrackNumber(trackNumberToFix: Int): Int = trackNumberToFix % 1000

    }
}
