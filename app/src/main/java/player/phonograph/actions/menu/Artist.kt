/*
 * Copyright (c) 2022 chr_56
 */

package player.phonograph.actions.menu

import com.github.chr56.android.menu_dsl.attach
import com.github.chr56.android.menu_dsl.menuItem
import player.phonograph.R
import player.phonograph.actions.actionAddToPlaylist
import player.phonograph.actions.actionDelete
import player.phonograph.actions.actionEnqueue
import player.phonograph.actions.actionPlay
import player.phonograph.actions.actionPlayNext
import player.phonograph.actions.activity
import player.phonograph.coil.CustomArtistImageStore
import player.phonograph.model.Artist
import player.phonograph.repo.loader.Songs
import player.phonograph.service.queue.ShuffleMode.NONE
import player.phonograph.service.queue.ShuffleMode.SHUFFLE
import player.phonograph.ui.activities.ArtistDetailActivity
import player.phonograph.ui.modules.tag.MultiTagBrowserActivity
import player.phonograph.ui.modules.web.LastFmDialog
import player.phonograph.util.theme.getTintedDrawable
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlin.random.Random

fun artistDetailToolbar(
    menu: Menu,
    context: Context,
    artist: Artist,
    @ColorInt iconColor: Int,
): Boolean = with(context) {
    attach(menu) {

        fun songs() = Songs.artist(context, artist.id)

        menuItem(title = getString(R.string.action_play)) { //id = R.id.action_shuffle_artist
            icon = getTintedDrawable(R.drawable.ic_play_arrow_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick { songs().actionPlay(NONE, 0) }
        }

        menuItem(title = getString(R.string.action_shuffle_artist)) { //id = R.id.action_shuffle_artist
            icon = getTintedDrawable(R.drawable.ic_shuffle_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                val songs = songs()
                songs.actionPlay(SHUFFLE, Random.nextInt(songs.size))
            }
        }


        menuItem(title = getString(R.string.action_play_next)) { //id = R.id.action_play_next
            icon = getTintedDrawable(R.drawable.ic_redo_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick { songs().actionPlayNext() }
        }


        menuItem(title = getString(R.string.action_add_to_playing_queue)) { //id = R.id.action_add_to_current_playing
            icon = getTintedDrawable(R.drawable.ic_library_add_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick { songs().actionEnqueue() }
        }

        menuItem(title = getString(R.string.action_add_to_playlist)) { //id = R.id.action_add_to_playlist
            icon = getTintedDrawable(R.drawable.ic_playlist_add_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick { songs().actionAddToPlaylist(context) }
        }

        menuItem(title = getString(R.string.set_artist_image)) { //id = R.id.action_set_artist_image
            icon = getTintedDrawable(R.drawable.ic_person_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                activity(context) {
                    it.startActivityForResult(
                        Intent.createChooser(
                            Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "image/*"
                            }, getString(R.string.pick_from_local_storage)
                        ),
                        ArtistDetailActivity.REQUEST_CODE_SELECT_IMAGE
                    )
                    true
                }
            }
        }


        menuItem(title = getString(R.string.reset_artist_image)) { //id = R.id.action_reset_artist_image
            icon = getTintedDrawable(R.drawable.ic_close_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                Toast.makeText(context, resources.getString(R.string.updating), Toast.LENGTH_SHORT)
                    .show()
                CustomArtistImageStore.instance(context)
                    .resetCustomArtistImage(context, artist.id, artist.name)
                true
            }
        }

        menuItem(title = getString(R.string.action_tag_editor)) { //id = R.id.action_tag_editor
            icon = getTintedDrawable(R.drawable.ic_library_music_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                MultiTagBrowserActivity.launch(context, ArrayList(songs().map { it.data }))
                true
            }
        }

        menuItem(title = getString(R.string.action_delete_from_device)) { //id = R.id.action_delete_from_device
            icon = getTintedDrawable(R.drawable.ic_delete_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                songs().actionDelete(context)
            }
        }


        menuItem(title = getString(R.string.biography)) { //id = R.id.action_biography
            icon = getTintedDrawable(R.drawable.ic_info_outline_white_24dp, iconColor)
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM
            onClick {
                if (context is FragmentActivity) {
                    LastFmDialog.from(artist).show(context.supportFragmentManager, "LastFmDialog")
                }
                true
            }
        }
    }
    true
}

