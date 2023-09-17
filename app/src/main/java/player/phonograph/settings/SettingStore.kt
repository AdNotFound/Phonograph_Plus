/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.settings

import player.phonograph.App
import player.phonograph.actions.click.mode.SongClickMode.FLAG_MASK_PLAY_QUEUE_IF_EMPTY
import player.phonograph.actions.click.mode.SongClickMode.SONG_PLAY_NOW
import player.phonograph.model.sort.FileSortMode
import player.phonograph.model.sort.SortMode
import player.phonograph.model.sort.SortRef
import player.phonograph.model.time.CalculationMode
import player.phonograph.model.time.Duration
import player.phonograph.util.time.past
import player.phonograph.util.time.recently
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val PREFERENCE_NAME = "setting_main"
val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(
            name = PREFERENCE_NAME,
            produceMigrations = {
                listOf(
                    SharedPreferencesMigration({ PreferenceManager.getDefaultSharedPreferences(it) })
                )
            }
        )


class Setting {
    //region Preferences

    // Appearance
    var homeTabConfigJsonString: String by stringPref(HOME_TAB_CONFIG, "")
    var coloredNotification: Boolean by booleanPref(COLORED_NOTIFICATION, true)
    var classicNotification: Boolean by booleanPref(CLASSIC_NOTIFICATION, false)
    var coloredAppShortcuts: Boolean by booleanPref(COLORED_APP_SHORTCUTS, true)
    var fixedTabLayout: Boolean by booleanPref(FIXED_TAB_LAYOUT, false)

    // Behavior-Retention
    var rememberLastTab: Boolean by booleanPref(REMEMBER_LAST_TAB, true)
    var lastPage: Int by intPref(LAST_PAGE, 0)
    var lastMusicChooser: Int by intPref(LAST_MUSIC_CHOOSER, 0)
    var nowPlayingScreenIndex: Int by intPref(NOW_PLAYING_SCREEN_ID, 0)

    // Behavior-File
    var imageSourceConfigJsonString: String by stringPref(IMAGE_SOURCE_CONFIG, "{}")

    // Behavior-Playing
    var songItemClickMode: Int
            by intPref(SONG_ITEM_CLICK_MODE, SONG_PLAY_NOW)
    var songItemClickExtraFlag: Int
            by intPref(SONG_ITEM_CLICK_EXTRA_FLAG, FLAG_MASK_PLAY_QUEUE_IF_EMPTY)
    var keepPlayingQueueIntact: Boolean by booleanPref(KEEP_PLAYING_QUEUE_INTACT, true)
    var rememberShuffle: Boolean by booleanPref(REMEMBER_SHUFFLE, true)
    var gaplessPlayback: Boolean by booleanPref(GAPLESS_PLAYBACK, false)
    var audioDucking: Boolean by booleanPref(AUDIO_DUCKING, true)
    var resumeAfterAudioFocusGain: Boolean by booleanPref(RESUME_AFTER_AUDIO_FOCUS_GAIN, false)
    var enableLyrics: Boolean by booleanPref(ENABLE_LYRICS, true)
    var broadcastSynchronizedLyrics: Boolean by booleanPref(BROADCAST_SYNCHRONIZED_LYRICS, true)
    var useLegacyStatusBarLyricsApi: Boolean by booleanPref(USE_LEGACY_STATUS_BAR_LYRICS_API, false)
    var broadcastCurrentPlayerState: Boolean by booleanPref(BROADCAST_CURRENT_PLAYER_STATE, true)

    // Behavior-Lyrics
    var synchronizedLyricsShow: Boolean by booleanPref(SYNCHRONIZED_LYRICS_SHOW, true)
    var displaySynchronizedLyricsTimeAxis: Boolean by booleanPref(DISPLAY_LYRICS_TIME_AXIS, true)

    private var _lastAddedCutOffMode: Int by intPref(LAST_ADDED_CUTOFF_MODE, CalculationMode.PAST.value)
    var lastAddedCutOffMode: CalculationMode
        get() = CalculationMode.from(_lastAddedCutOffMode) ?: CalculationMode.PAST
        set(value) {
            _lastAddedCutOffMode = value.value
        }

    private var _lastAddedCutOffDuration: String by stringPref(LAST_ADDED_CUTOFF_DURATION, Duration.Week(3).serialise())
    var lastAddedCutOffDuration: Duration
        get() = Duration.from(_lastAddedCutOffDuration) ?: Duration.Week(3)
        set(value) {
            _lastAddedCutOffDuration = value.serialise()
        }

    val lastAddedCutoffTimeStamp: Long
        get() = System.currentTimeMillis() - when (lastAddedCutOffMode) {
            CalculationMode.PAST   -> past(lastAddedCutOffDuration)
            CalculationMode.RECENT -> recently(lastAddedCutOffDuration)
        }

    // Upgrade
    var checkUpgradeAtStartup: Boolean by booleanPref(CHECK_UPGRADE_AT_STARTUP, false)

    // List-SortMode
    private var _songSortMode: String by stringPref(
        SONG_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var songSortMode: SortMode
        get() = SortMode.deserialize(_songSortMode)
        set(value) {
            _songSortMode = value.serialize()
        }

    private var _albumSortMode: String by stringPref(
        ALBUM_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var albumSortMode: SortMode
        get() = SortMode.deserialize(_albumSortMode)
        set(value) {
            _albumSortMode = value.serialize()
        }

    private var _artistSortMode: String by stringPref(
        ARTIST_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var artistSortMode: SortMode
        get() = SortMode.deserialize(_artistSortMode)
        set(value) {
            _artistSortMode = value.serialize()
        }

    private var _genreSortMode: String by stringPref(
        GENRE_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var genreSortMode: SortMode
        get() = SortMode.deserialize(_genreSortMode)
        set(value) {
            _genreSortMode = value.serialize()
        }

    private var _fileSortMode: String by stringPref(
        FILE_SORT_MODE,
        FileSortMode(SortRef.ID, false).serialize()
    )
    var fileSortMode: FileSortMode
        get() = FileSortMode.deserialize(_fileSortMode)
        set(value) {
            _fileSortMode = value.serialize()
        }

    private var _collectionSortMode: String by stringPref(
        SONG_COLLECTION_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var collectionSortMode: SortMode
        get() = SortMode.deserialize(_collectionSortMode)
        set(value) {
            _collectionSortMode = value.serialize()
        }

    private var _playlistSortMode: String by stringPref(
        PLAYLIST_SORT_MODE,
        SortMode(SortRef.ID, false).serialize()
    )
    var playlistSortMode: SortMode
        get() = SortMode.deserialize(_playlistSortMode)
        set(value) {
            _playlistSortMode = value.serialize()
        }

    // List-Appearance
    /*  see also [DisplaySetting] */
    var albumArtistColoredFooters by booleanPref(ALBUM_ARTIST_COLORED_FOOTERS, true)
    var showFileImages by booleanPref(SHOW_FILE_IMAGINES, false)

    // SleepTimer
    var lastSleepTimerValue: Int by intPref(LAST_SLEEP_TIMER_VALUE, 30)
    var nextSleepTimerElapsedRealTime: Long by longPref(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, -1L)
    var sleepTimerFinishMusic: Boolean by booleanPref(SLEEP_TIMER_FINISH_SONG, false)

    // Misc
    var ignoreUpgradeDate: Long by longPref(IGNORE_UPGRADE_DATE, 0)
    var pathFilterExcludeMode: Boolean by booleanPref(PATH_FILTER_EXCLUDE_MODE, true)

    // Compatibility
    var useLegacyFavoritePlaylistImpl: Boolean by booleanPref(
        USE_LEGACY_FAVORITE_PLAYLIST_IMPL,
        false
    )
    var useLegacyListFilesImpl: Boolean by booleanPref(USE_LEGACY_LIST_FILES_IMPL, false)
    var playlistFilesOperationBehaviour: String by stringPref(
        PLAYLIST_FILES_OPERATION_BEHAVIOUR,
        PLAYLIST_OPS_BEHAVIOUR_AUTO
    )

    var useLegacyDetailDialog: Boolean by booleanPref(USE_LEGACY_DETAIL_DIALOG, false)

    //endregion

    companion object {
        //region Singleton
        private var singleton: Setting? = null
        val instance: Setting
            @JvmStatic get() {
                if (singleton == null) singleton = Setting()
                return singleton!!
            }

        // @JvmStatic
        // fun instance(): Setting = instance


        //endregion
    }


}

class SettingFlowStore(context: Context) {

    //region Preferences

    // Appearance
    val homeTabConfigJsonString: Flow<String>
        get() = from(stringPreferencesKey(HOME_TAB_CONFIG), "")
    val coloredNotification: Flow<Boolean>
        get() = from(booleanPreferencesKey(COLORED_NOTIFICATION), true)
    val classicNotification: Flow<Boolean>
        get() = from(booleanPreferencesKey(CLASSIC_NOTIFICATION), false)
    val coloredAppShortcuts: Flow<Boolean>
        get() = from(booleanPreferencesKey(COLORED_APP_SHORTCUTS), true)
    val fixedTabLayout: Flow<Boolean>
        get() = from(booleanPreferencesKey(FIXED_TAB_LAYOUT), false)

    // Behavior-Retention
    val rememberLastTab: Flow<Boolean>
        get() = from(booleanPreferencesKey(REMEMBER_LAST_TAB), true)
    val lastPage: Flow<Int>
        get() = from(intPreferencesKey(LAST_PAGE), 0)
    val lastMusicChooser: Flow<Int>
        get() = from(intPreferencesKey(LAST_MUSIC_CHOOSER), 0)
    val nowPlayingScreenIndex: Flow<Int>
        get() = from(intPreferencesKey(NOW_PLAYING_SCREEN_ID), 0)

    // Behavior-File
    val imageSourceConfigJsonString: Flow<String>
        get() = from(stringPreferencesKey(IMAGE_SOURCE_CONFIG), "{}")

    // Behavior-Playing
    val songItemClickMode: Flow<Int>
        get() = from(intPreferencesKey(SONG_ITEM_CLICK_MODE), SONG_PLAY_NOW)
    val songItemClickExtraFlag: Flow<Int>
        get() = from(intPreferencesKey(SONG_ITEM_CLICK_EXTRA_FLAG), FLAG_MASK_PLAY_QUEUE_IF_EMPTY)
    val keepPlayingQueueIntact: Flow<Boolean>
        get() = from(booleanPreferencesKey(KEEP_PLAYING_QUEUE_INTACT), true)
    val rememberShuffle: Flow<Boolean>
        get() = from(booleanPreferencesKey(REMEMBER_SHUFFLE), true)
    val gaplessPlayback: Flow<Boolean>
        get() = from(booleanPreferencesKey(GAPLESS_PLAYBACK), false)
    val audioDucking: Flow<Boolean>
        get() = from(booleanPreferencesKey(AUDIO_DUCKING), true)
    val resumeAfterAudioFocusGain: Flow<Boolean>
        get() = from(booleanPreferencesKey(RESUME_AFTER_AUDIO_FOCUS_GAIN), true)
    val enableLyrics: Flow<Boolean>
        get() = from(booleanPreferencesKey(ENABLE_LYRICS), true)
    val broadcastSynchronizedLyrics: Flow<Boolean>
        get() = from(booleanPreferencesKey(BROADCAST_SYNCHRONIZED_LYRICS), true)
    val useLegacyStatusBarLyricsApi: Flow<Boolean>
        get() = from(booleanPreferencesKey(USE_LEGACY_STATUS_BAR_LYRICS_API), false)
    val broadcastCurrentPlayerState: Flow<Boolean>
        get() = from(booleanPreferencesKey(BROADCAST_CURRENT_PLAYER_STATE), true)

    // Behavior-Lyrics
    val synchronizedLyricsShow: Flow<Boolean>
        get() = from(booleanPreferencesKey(SYNCHRONIZED_LYRICS_SHOW), true)
    val displaySynchronizedLyricsTimeAxis: Flow<Boolean>
        get() = from(booleanPreferencesKey(DISPLAY_LYRICS_TIME_AXIS), true)

    private val _lastAddedCutOffMode: Flow<Int>
        get() = from(intPreferencesKey(LAST_ADDED_CUTOFF_MODE), CalculationMode.PAST.value)

    private val _lastAddedCutOffDuration: Flow<String>
        get() = from(stringPreferencesKey(LAST_ADDED_CUTOFF_DURATION), Duration.Week(3).serialise())

    val lastAddedCutoffTimeStamp: Flow<Long>
        get() = _lastAddedCutOffDuration.combine(_lastAddedCutOffMode) { durationPref, modePref ->
            val calculationMode = CalculationMode.from(modePref)
            val duration = Duration.from(durationPref)
            if (calculationMode != null && duration != null) {
                System.currentTimeMillis() - when (calculationMode) {
                    CalculationMode.PAST   -> past(duration)
                    CalculationMode.RECENT -> recently(duration)
                }
            } else {
                System.currentTimeMillis()
            }
        }

    // Upgrade
    val checkUpgradeAtStartup: Flow<Boolean>
        get() = from(booleanPreferencesKey(CHECK_UPGRADE_AT_STARTUP), false)

    // List-SortMode
    private val _songSortMode: Flow<String>
        get() = from(stringPreferencesKey(SONG_SORT_MODE), SortMode(SortRef.ID).serialize())
    val songSortMode: Flow<SortMode>
        get() = _songSortMode.map { SortMode.deserialize(it) }

    private val _albumSortMode: Flow<String>
        get() = from(stringPreferencesKey(ALBUM_SORT_MODE), SortMode(SortRef.ID).serialize())
    val albumSortMode: Flow<SortMode>
        get() = _albumSortMode.map { SortMode.deserialize(it) }

    private val _artistSortMode: Flow<String>
        get() = from(stringPreferencesKey(ARTIST_SORT_MODE), SortMode(SortRef.ID).serialize())
    val artistSortMode: Flow<SortMode>
        get() = _artistSortMode.map { SortMode.deserialize(it) }

    private val _genreSortMode: Flow<String>
        get() = from(stringPreferencesKey(GENRE_SORT_MODE), SortMode(SortRef.ID).serialize())
    val genreSortMode: Flow<SortMode>
        get() = _genreSortMode.map { SortMode.deserialize(it) }

    private val _fileSortMode: Flow<String>
        get() = from(stringPreferencesKey(FILE_SORT_MODE), FileSortMode(SortRef.ID).serialize())
    val fileSortMode: Flow<FileSortMode>
        get() = _fileSortMode.map { FileSortMode.deserialize(it) }

    private val _collectionSortMode: Flow<String>
        get() = from(stringPreferencesKey(SONG_COLLECTION_SORT_MODE), FileSortMode(SortRef.ID).serialize())
    val collectionSortMode: Flow<SortMode>
        get() = _collectionSortMode.map { SortMode.deserialize(it) }

    private val _playlistSortMode: Flow<String>
        get() = from(stringPreferencesKey(PLAYLIST_SORT_MODE), FileSortMode(SortRef.ID).serialize())
    val playlistSortMode: Flow<SortMode>
        get() = _playlistSortMode.map { SortMode.deserialize(it) }


    // List-Appearance
    /*  see also [DisplaySetting] */
    val albumArtistColoredFooters: Flow<Boolean>
        get() = from(booleanPreferencesKey(ALBUM_ARTIST_COLORED_FOOTERS), true)
    val showFileImages: Flow<Boolean>
        get() = from(booleanPreferencesKey(SHOW_FILE_IMAGINES), false)

    // SleepTimer
    val lastSleepTimerValue: Flow<Int>
        get() = from(intPreferencesKey(LAST_SLEEP_TIMER_VALUE), 30)
    val nextSleepTimerElapsedRealTime: Flow<Long>
        get() = from(longPreferencesKey(NEXT_SLEEP_TIMER_ELAPSED_REALTIME), -1L)
    val sleepTimerFinishMusic: Flow<Boolean>
        get() = from(booleanPreferencesKey(SLEEP_TIMER_FINISH_SONG), false)

    // Misc
    val ignoreUpgradeDate: Flow<Long>
        get() = from(longPreferencesKey(IGNORE_UPGRADE_DATE), 0)
    val pathFilterExcludeMode: Flow<Boolean>
        get() = from(booleanPreferencesKey(PATH_FILTER_EXCLUDE_MODE), true)

    // Compatibility
    val useLegacyFavoritePlaylistImpl: Flow<Boolean>
        get() = from(booleanPreferencesKey(USE_LEGACY_FAVORITE_PLAYLIST_IMPL), false)
    val useLegacyListFilesImpl: Flow<Boolean>
        get() = from(booleanPreferencesKey(USE_LEGACY_LIST_FILES_IMPL), false)
    val playlistFilesOperationBehaviour: Flow<String>
        get() = from(stringPreferencesKey(PLAYLIST_FILES_OPERATION_BEHAVIOUR), PLAYLIST_OPS_BEHAVIOUR_AUTO)
    val useLegacyDetailDialog: Flow<Boolean>
        get() = from(booleanPreferencesKey(USE_LEGACY_DETAIL_DIALOG), false)

    //endregion

    private val ds = context.dataStore
    private fun <T> from(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        ds.data.map { preferences -> preferences[key] ?: defaultValue }
}


//region Keys

// Appearance
const val HOME_TAB_CONFIG = "home_tab_config"
const val COLORED_NOTIFICATION = "colored_notification"
const val CLASSIC_NOTIFICATION = "classic_notification"
const val COLORED_APP_SHORTCUTS = "colored_app_shortcuts"
const val FIXED_TAB_LAYOUT = "fixed_tab_layout"

// Behavior-Retention
const val REMEMBER_LAST_TAB = "remember_last_tab"
const val LAST_PAGE = "last_start_page"
const val LAST_MUSIC_CHOOSER = "last_music_chooser"
const val NOW_PLAYING_SCREEN_ID = "now_playing_screen_id"

// Behavior-File
const val IMAGE_SOURCE_CONFIG = "image_source_config"

// Behavior-Playing
const val SONG_ITEM_CLICK_MODE = "song_item_click_extra_flag"
const val SONG_ITEM_CLICK_EXTRA_FLAG = "song_item_click_extra_mode"
const val KEEP_PLAYING_QUEUE_INTACT = "keep_playing_queue_intact"
const val REMEMBER_SHUFFLE = "remember_shuffle"
const val AUDIO_DUCKING = "audio_ducking"
const val RESUME_AFTER_AUDIO_FOCUS_GAIN = "resume_after_audio_focus_gain"
const val GAPLESS_PLAYBACK = "gapless_playback"
const val ENABLE_LYRICS = "enable_lyrics"
const val BROADCAST_SYNCHRONIZED_LYRICS = "synchronized_lyrics_send"
const val USE_LEGACY_STATUS_BAR_LYRICS_API = "use_legacy_status_bar_lyrics_api"
const val BROADCAST_CURRENT_PLAYER_STATE = "broadcast_current_player_state"

// Behavior-Lyrics
const val SYNCHRONIZED_LYRICS_SHOW = "synchronized_lyrics_show"
const val DISPLAY_LYRICS_TIME_AXIS = "display_lyrics_time_axis"

// List-Cutoff
const val LAST_ADDED_CUTOFF_MODE = "last_added_cutoff_mode"
const val LAST_ADDED_CUTOFF_DURATION = "last_added_cutoff_duration"

// Upgrade
const val CHECK_UPGRADE_AT_STARTUP = "check_upgrade_at_startup"

// List-SortMode
const val SONG_SORT_MODE = "song_sort_mode"
const val ALBUM_SORT_MODE = "album_sort_mode"
const val ARTIST_SORT_MODE = "artist_sort_mode"
const val GENRE_SORT_MODE = "genre_sort_mode"

const val FILE_SORT_MODE = "file_sort_mode"
const val SONG_COLLECTION_SORT_MODE = "song_collection_sort_mode"

const val PLAYLIST_SORT_MODE = "playlist_sort_mode"

// List-Appearance
/*  see also [DisplaySetting] */
const val ALBUM_ARTIST_COLORED_FOOTERS = "album_artist_colored_footers"
const val SHOW_FILE_IMAGINES = "show_file_imagines"

// SleepTimer
const val LAST_SLEEP_TIMER_VALUE = "last_sleep_timer_value"
const val NEXT_SLEEP_TIMER_ELAPSED_REALTIME = "next_sleep_timer_elapsed_real_time"
const val SLEEP_TIMER_FINISH_SONG = "sleep_timer_finish_music"

// Misc
const val IGNORE_UPGRADE_DATE = "ignore_upgrade_date"
const val PATH_FILTER_EXCLUDE_MODE = "path_filter_exclude_mode"

// compatibility
const val USE_LEGACY_FAVORITE_PLAYLIST_IMPL = "use_legacy_favorite_playlist_impl"
const val USE_LEGACY_LIST_FILES_IMPL = "use_legacy_list_files_impl"
const val PLAYLIST_FILES_OPERATION_BEHAVIOUR = "playlist_files_operation_behaviour"
const val USE_LEGACY_DETAIL_DIALOG = "use_legacy_detail_dialog"

// unused & deprecated

const val LEGACY_LAST_ADDED_CUTOFF = "last_added_interval"
const val INTERVAL_TODAY = "today"
const val INTERVAL_PAST_SEVEN_DAYS = "past_seven_days"
const val INTERVAL_PAST_FOURTEEN_DAYS = "past_fourteen_days"
const val INTERVAL_PAST_ONE_MONTH = "past_one_month"
const val INTERVAL_PAST_THREE_MONTHS = "past_three_months"
const val INTERVAL_THIS_WEEK = "this_week"
const val INTERVAL_THIS_MONTH = "this_month"
const val INTERVAL_THIS_YEAR = "this_year"
const val AUTO_DOWNLOAD_IMAGES_POLICY = "auto_download_images_policy"
const val DOWNLOAD_IMAGES_POLICY_ALWAYS = "always"
const val DOWNLOAD_IMAGES_POLICY_ONLY_WIFI = "only_wifi"
const val DOWNLOAD_IMAGES_POLICY_NEVER = "never"
const val INITIALIZED_BLACKLIST = "initialized_blacklist"
const val PREVIOUS_VERSION = "last_changelog_version"
const val FORCE_SQUARE_ALBUM_COVER = "force_square_album_art"
const val IGNORE_UPGRADE_VERSION_CODE = "ignore_upgrade_version_code"
const val IGNORE_MEDIA_STORE_ARTWORK = "ignore_media_store_artwork"
//endregion

//region Values
// StringArrayPref
const val PLAYLIST_OPS_BEHAVIOUR_AUTO = "auto"
const val PLAYLIST_OPS_BEHAVIOUR_FORCE_SAF = "force_saf"
const val PLAYLIST_OPS_BEHAVIOUR_FORCE_LEGACY = "force_legacy"
//endregion


// region Delegate

private fun stringPref(key: String, defaultValue: String) = Delegate(stringPreferencesKey(key), defaultValue)
private fun booleanPref(key: String, defaultValue: Boolean) = Delegate(booleanPreferencesKey(key), defaultValue)
private fun intPref(key: String, defaultValue: Int) = Delegate(intPreferencesKey(key), defaultValue)
private fun longPref(key: String, defaultValue: Long) = Delegate(longPreferencesKey(key), defaultValue)

private class Delegate<T>(private val key: Preferences.Key<T>, private val defaultValue: T) :
        ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = runBlocking {
        requireDatastore().data.first()[key] ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        runBlocking {
            requireDatastore().edit { mutablePreferences ->
                mutablePreferences[key] = value
            }
        }
    }

    companion object {
        private fun requireDatastore(context: Context = App.instance) = context.dataStore
    }
}
//endregion
