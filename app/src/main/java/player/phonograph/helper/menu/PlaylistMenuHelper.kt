package player.phonograph.helper.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import player.phonograph.*
import player.phonograph.dialogs.AddToPlaylistDialog
import player.phonograph.dialogs.DeletePlaylistDialog
import player.phonograph.dialogs.RenamePlaylistDialog
import player.phonograph.helper.MusicPlayerRemote
import player.phonograph.loader.PlaylistSongLoader
import player.phonograph.misc.WeakContextAsyncTask
import player.phonograph.model.AbsCustomPlaylist
import player.phonograph.model.Playlist
import player.phonograph.model.Song
import player.phonograph.model.smartplaylist.AbsSmartPlaylist
import player.phonograph.model.smartplaylist.HistoryPlaylist
import player.phonograph.model.smartplaylist.LastAddedPlaylist
import player.phonograph.model.smartplaylist.MyTopTracksPlaylist
import player.phonograph.ui.activities.base.AbsMusicServiceActivity
import player.phonograph.util.PlaylistsUtil
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
object PlaylistMenuHelper {

    @JvmStatic
    fun handleMenuClick(activity: AppCompatActivity, playlist: Playlist, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(
                    ArrayList(getPlaylistSongs(activity, playlist)), 0, true
                )
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(ArrayList(getPlaylistSongs(activity, playlist)))
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(ArrayList(getPlaylistSongs(activity, playlist)))
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(getPlaylistSongs(activity, playlist))
                    .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_rename_playlist -> {
                RenamePlaylistDialog.create(playlist.id).show(activity.supportFragmentManager, "RENAME_PLAYLIST")
                return true
            }
            R.id.action_delete_playlist -> {
                DeletePlaylistDialog.create(listOf(playlist)).show(
                    activity.supportFragmentManager, "DELETE_PLAYLIST"
                )
                return true
            }
            R.id.action_save_playlist -> {

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "audio/x-mpegurl"
                    putExtra(
                        Intent.EXTRA_TITLE,
                        playlist.name +
                            SimpleDateFormat(
                                "_yy-MM-dd_HH-mm", Locale.getDefault()
                            ).format(Calendar.getInstance().time)
                    )
                }
//                App.instance.taskManager.addTask(
//                    Task(TASK_ID_SAVE_PLAYLIST, null).also {
//                        it.action = Task.ACTION_SAVE_PLAYLIST
//
//                        if (playlist is AbsSmartPlaylist) {
//                            it.data =
//                                when (playlist) {
//                                    is HistoryPlaylist -> TASK_DATA_HistoryPlaylist
//                                    is LastAddedPlaylist -> TASK_DATA_LastAddedPlaylist
//                                    is MyTopTracksPlaylist -> TASK_DATA_MyTopTracksPlaylist
//                                    else -> "Smart"
//                                }
//                        } else {
//                            it.data = TASK_DATA_Normal
//                            it.num = playlist.id
//                        }
//                    }
//                )
                startActivityForResult(activity, intent, REQUEST_CODE_SAVE_PLAYLIST, null)

                val msgBundle = Bundle()
                if (playlist is AbsSmartPlaylist) {
                    val type: String = when (playlist) {
                        is HistoryPlaylist -> HistoryPlaylist
                        is LastAddedPlaylist -> LastAddedPlaylist
                        is MyTopTracksPlaylist -> MyTopTracksPlaylist
                        else -> "Smart"
                    }
                    msgBundle.putString(TYPE, type)
                } else {
                    msgBundle.putString(TYPE, NormalPlaylist)
                    msgBundle.putLong(PLAYLIST_ID, playlist.id)
                }

                (activity as AbsMusicServiceActivity).handler?.let { handler ->
                    val msg = Message.obtain(handler,REQUEST_CODE_SAVE_PLAYLIST).also {
                        it.data = msgBundle
                    }
                    handler.sendMessageDelayed(msg, 10)
                }

                return true
            }
        }
        return false
    }

    private fun getPlaylistSongs(activity: Activity, playlist: Playlist): List<Song> {
        return if (playlist is AbsCustomPlaylist) playlist.getSongs(activity)
        else PlaylistSongLoader.getPlaylistSongList(activity, playlist.id)
    }

//    private class SavePlaylistAsyncTask(context: Context?) :
//        WeakContextAsyncTask<Playlist?, String?, String?>(context) {
//        override fun doInBackground(vararg params: Playlist?): String? {
//            return try {
//                String.format(
//                    App.instance.applicationContext.getString(R.string.saved_playlist_to),
//                    PlaylistsUtil.savePlaylist(App.instance.applicationContext, params[0])
//                )
//            } catch (e: IOException) {
//                e.printStackTrace()
//                String.format(
//                    App.instance.applicationContext.getString(R.string.failed_to_save_playlist), e
//                )
//            }
//        }
//
//        override fun onPostExecute(string: String?) {
//            super.onPostExecute(string)
//            val context = context
//            if (context != null) {
//                Toast.makeText(context, string, Toast.LENGTH_LONG).show()
//            }
//        }
//    }

//    /**
//     * CALL this IN onActivityResult()
//     */
//    @JvmStatic
//    fun handleSavePlaylist(activity: Activity, uri: Uri) {
//
//        val taskManager = App.instance.taskManager
//
//        val task = taskManager.findTask(REQUEST_CODE_SAVE_PLAYLIST) ?: return
//        val action: String = task.action ?: return
//
//        if (action == Task.ACTION_SAVE_PLAYLIST) {
//            val data: String = task.data ?: return
//            var result: Short = -1
//            try {
//                when (data) {
//                    NormalPlaylist ->
//                        task.num?.let { result = savePlaylist(activity, uri, it) }
//                    MyTopTracksPlaylist ->
//                        result = savePlaylist(activity, uri, MyTopTracksPlaylist(activity))
//                    LastAddedPlaylist ->
//                        result = savePlaylist(activity, uri, LastAddedPlaylist(activity))
//                    HistoryPlaylist ->
//                        result = savePlaylist(activity, uri, HistoryPlaylist(activity))
//                    else -> {
//                        result = -1
//                    }
//                }
//
//                // report result
//                if (result.toInt() != 0) {
//                    Toast.makeText(
//                        activity, activity.resources.getText(R.string.failed), Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        activity, activity.resources.getText(R.string.success), Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } finally {
//                taskManager.removeTask(REQUEST_CODE_SAVE_PLAYLIST)
//            }
//        }
//    }
}
