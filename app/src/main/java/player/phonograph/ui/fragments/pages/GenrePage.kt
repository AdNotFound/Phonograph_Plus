/*
 * Copyright (c) 2022 chr_56 & Abou Zeid (kabouzeid) (original author)
 */

package player.phonograph.ui.fragments.pages

import player.phonograph.R
import player.phonograph.model.Genre
import player.phonograph.model.sort.SortRef
import player.phonograph.repo.loader.Genres
import player.phonograph.ui.adapter.DisplayAdapter
import player.phonograph.ui.fragments.pages.adapter.GenreDisplayAdapter
import player.phonograph.ui.fragments.pages.util.GenrePageDisplayConfig
import player.phonograph.ui.fragments.pages.util.PageDisplayConfig
import androidx.fragment.app.viewModels
import android.content.Context
import kotlinx.coroutines.CoroutineScope

class GenrePage : AbsDisplayPage<Genre, DisplayAdapter<Genre>>() {

    override val viewModel: AbsDisplayPageViewModel<Genre> get() = _viewModel

    private val _viewModel: GenrePageViewModel by viewModels()

    class GenrePageViewModel : AbsDisplayPageViewModel<Genre>() {
        override suspend fun loadDataSetImpl(context: Context, scope: CoroutineScope): Collection<Genre> {
            return Genres.all(context)
        }

        override val headerTextRes: Int get() = R.plurals.item_genres
    }

    override val displayConfig: PageDisplayConfig = GenrePageDisplayConfig

    override fun initAdapter(): DisplayAdapter<Genre> {
        return GenreDisplayAdapter(hostFragment.mainActivity)
    }

    override val availableSortRefs: Array<SortRef>
        get() = arrayOf(
            SortRef.DISPLAY_NAME,
            SortRef.SONG_COUNT,
        )

    override fun allowColoredFooter(): Boolean = false

    companion object {
        const val TAG = "GenrePage"
    }
}
