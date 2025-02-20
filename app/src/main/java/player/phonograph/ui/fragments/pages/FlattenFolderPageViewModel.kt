/*
 * Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.ui.fragments.pages

import player.phonograph.model.Song
import player.phonograph.model.SongCollection
import player.phonograph.model.sort.SortRef
import player.phonograph.repo.mediastore.loaders.SongCollectionLoader
import player.phonograph.settings.Keys
import player.phonograph.settings.Setting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlattenFolderPageViewModel : ViewModel() {

    /**
     * true if browsing folders
     */
    val mainViewMode: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val _folders: MutableStateFlow<List<SongCollection>> = MutableStateFlow(emptyList())
    val folders = _folders.asStateFlow()

    private val _currentSongs: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
    val currentSongs = _currentSongs.asStateFlow()

    private var _currentPosition: Int = -1
    val currentFolder get() = _folders.value.getOrNull(_currentPosition)


    fun loadFolders(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _folders.emit(
                SongCollectionLoader.all(context = context).toMutableList().sort(context)
            )
        }
    }

    fun browseFolder(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentPosition = position
            loadSongs()
            mainViewMode.emit(false)
        }
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentSongs.emit(currentFolder?.songs ?: emptyList())
        }
    }

    private fun MutableList<SongCollection>.sort(context: Context): List<SongCollection> {
        val mode = Setting(context).Composites[Keys.collectionSortMode].data
        sortBy {
            when (mode.sortRef) {
                SortRef.DISPLAY_NAME -> it.name
                else                 -> null
            }
        }
        if (mode.revert) reverse()
        return this
    }
}