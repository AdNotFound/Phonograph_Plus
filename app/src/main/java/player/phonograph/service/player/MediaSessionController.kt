/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.service.player

import coil.request.Disposable
import player.phonograph.BuildConfig
import player.phonograph.R
import player.phonograph.model.Song
import player.phonograph.service.MusicService
import player.phonograph.service.MusicService.Companion.MEDIA_SESSION_ACTION_TOGGLE_REPEAT
import player.phonograph.service.MusicService.Companion.MEDIA_SESSION_ACTION_TOGGLE_SHUFFLE
import player.phonograph.service.notification.PlayingNotificationManger
import player.phonograph.service.queue.RepeatMode
import player.phonograph.service.queue.ShuffleMode
import player.phonograph.service.util.MediaButtonIntentReceiver
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_NUM_TRACKS
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_YEAR
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING

class MediaSessionController(
    private val musicService: MusicService,
) {
    lateinit var mediaSession: MediaSessionCompat
        private set

    fun setupMediaSession(callback: MediaSessionCompat.Callback) {
        val mediaButtonReceiverComponentName = ComponentName(
            musicService.applicationContext,
            MediaButtonIntentReceiver::class.java
        )
        val mediaButtonReceiverPendingIntent =
            PendingIntent.getBroadcast(
                musicService.applicationContext,
                0,
                Intent(Intent.ACTION_MEDIA_BUTTON).apply {
                    component = mediaButtonReceiverComponentName
                },
                PendingIntent.FLAG_IMMUTABLE
            )

        mediaSession =
            MediaSessionCompat(
                musicService,
                BuildConfig.APPLICATION_ID,
                mediaButtonReceiverComponentName,
                mediaButtonReceiverPendingIntent
            )

        mediaSession.setCallback(callback)

        // fixme remove deprecation
        @Suppress("DEPRECATION")
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
        )
        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
    }


    private val sessionPlaybackStateBuilder
        get() =
            PlaybackStateCompat.Builder().setActions(availableActions)


    fun updatePlaybackState(isPlaying: Boolean, songProgressMillis: Long) {
        mediaSession.setPlaybackState(
            sessionPlaybackStateBuilder.setCustomActions(musicService)
                .setState(if (isPlaying) STATE_PLAYING else STATE_PAUSED, songProgressMillis, musicService.speed)
                .build()
        )
    }


    private fun PlaybackStateCompat.Builder.setCustomActions(musicService: MusicService): PlaybackStateCompat.Builder {
        addCustomAction(
            MEDIA_SESSION_ACTION_TOGGLE_SHUFFLE,
            musicService.getString(R.string.action_shuffle_mode),
            when (musicService.queueManager.shuffleMode) {
                ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_white_24dp
                ShuffleMode.NONE    -> R.drawable.ic_shuffle_disabled_white_24dp
            }
        )
        addCustomAction(
            MEDIA_SESSION_ACTION_TOGGLE_REPEAT,
            musicService.getString(R.string.action_repeat_mode),
            when (musicService.queueManager.repeatMode) {
                RepeatMode.REPEAT_QUEUE       -> R.drawable.ic_repeat_white_24dp
                RepeatMode.REPEAT_SINGLE_SONG -> R.drawable.ic_repeat_one_white_24dp
                RepeatMode.NONE               -> R.drawable.ic_repeat_off_white_24dp
            }
        )
        return this
    }

    @Suppress("SameParameterValue")
    private fun fillMetadata(song: Song, pos: Long, total: Long, bitmap: Bitmap?) =
        MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_TITLE, song.title)
            putLong(METADATA_KEY_DURATION, song.duration)
            putString(METADATA_KEY_ALBUM, song.albumName)
            putString(METADATA_KEY_ARTIST, song.artistName)
            putString(METADATA_KEY_ALBUM_ARTIST, song.artistName)
            putLong(METADATA_KEY_YEAR, song.year.toLong())
            putBitmap(METADATA_KEY_ALBUM_ART, bitmap)
            putLong(METADATA_KEY_TRACK_NUMBER, pos)
            putLong(METADATA_KEY_NUM_TRACKS, total)
        }

    fun updateMetaData(song: Song, pos: Long, total: Long, loadCover: Boolean) {
        if (song.id == -1L) {
            mediaSession.setMetadata(null)
        } else {
            val metadata = fillMetadata(song, pos, total, null)
            mediaSession.setMetadata(metadata.build())
            if (loadCover || Build.VERSION.SDK_INT >= PlayingNotificationManger.VERSION_SET_COVER_USING_METADATA) {
                disposable?.dispose()
                disposable = musicService.coverLoader.load(song) { bitmap, _ ->
                    metadata.putBitmap(METADATA_KEY_ALBUM_ART, bitmap)
                    mediaSession.setMetadata(metadata.build())
                }
            }
        }
    }

    private var disposable: Disposable? = null

    companion object {
        const val availableActions = PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SET_PLAYBACK_SPEED or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_SEEK_TO
    }
}