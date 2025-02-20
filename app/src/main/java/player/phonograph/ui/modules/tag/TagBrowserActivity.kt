/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.ui.modules.tag

import lib.phonograph.misc.CreateFileStorageAccessTool
import lib.phonograph.misc.ICreateFileStorageAccess
import lib.phonograph.misc.IOpenFileStorageAccess
import lib.phonograph.misc.OpenFileStorageAccessTool
import player.phonograph.R
import player.phonograph.model.Song
import player.phonograph.repo.loader.Songs
import player.phonograph.ui.compose.ComposeThemeActivity
import player.phonograph.ui.compose.PhonographTheme
import player.phonograph.ui.modules.web.IWebSearchRequester
import player.phonograph.ui.modules.web.LastFmDialog
import player.phonograph.ui.modules.web.WebSearchLauncher
import player.phonograph.ui.modules.web.WebSearchTool
import player.phonograph.util.debug
import util.phonograph.tagsources.Source
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagBrowserActivity :
        ComposeThemeActivity(),
        IWebSearchRequester,
        ICreateFileStorageAccess,
        IOpenFileStorageAccess {

    private val viewModel: TagBrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        createFileStorageAccessTool.register(lifecycle, activityResultRegistry)
        openFileStorageAccessTool.register(lifecycle, activityResultRegistry)
        webSearchTool.register(lifecycle, activityResultRegistry)
        val song = parseIntent(this, intent)
        viewModel.updateSong(this, song)
        super.onCreate(savedInstanceState)

        setContent {
            val highlightColor by primaryColor.collectAsState()
            TagEditor(viewModel, highlightColor, onBackPressedDispatcher, webSearchTool)
        }
        onBackPressedDispatcher.addCallback {
            if (viewModel.pendingEditRequests.isNotEmpty()) {
                viewModel.exitWithoutSavingDialogState.show()
            } else {
                finish()
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.color.collect { color ->
                    if (color != null) primaryColor.update { color }
                }
            }
        }
    }

    override val openFileStorageAccessTool: OpenFileStorageAccessTool = OpenFileStorageAccessTool()
    override val createFileStorageAccessTool: CreateFileStorageAccessTool = CreateFileStorageAccessTool()
    override val webSearchTool: WebSearchTool = WebSearchTool()

    companion object {

        private const val PATH = "PATH"
        private fun parseIntent(context: Context, intent: Intent): Song =
            Songs.path(context, intent.extras?.getString(PATH).orEmpty())

        fun launch(context: Context, path: String) {
            context.startActivity(
                Intent(context, TagBrowserActivity::class.java).apply {
                    putExtra(PATH, path)
                }
            )
        }
    }
}

@Composable
private fun TagEditor(
    viewModel: TagBrowserViewModel,
    highlightColor: Color,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    webSearchTool: WebSearchTool,
) {
    PhonographTheme(highlightColor) {
        val scaffoldState = rememberScaffoldState()
        val editable by viewModel.editable.collectAsState()
        Scaffold(
            Modifier.statusBarsPadding(),
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(if (editable) R.string.action_tag_editor else R.string.label_details)
                        )
                    },
                    navigationIcon = {
                        Box(Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.ArrowBack, null,
                                Modifier.clickable {
                                    onBackPressedDispatcher.onBackPressed()
                                }
                            )
                        }
                    },
                    actions = {
                        RequestWebSearch(viewModel, webSearchTool)
                        if (editable) {
                            IconButton(onClick = { viewModel.saveConfirmationDialogState.show() }) {
                                Icon(painterResource(id = R.drawable.ic_save_white_24dp), stringResource(R.string.save))
                            }
                        } else {
                            IconButton(onClick = { viewModel.updateEditable(true) }) {
                                Icon(painterResource(id = R.drawable.ic_edit_white_24dp), stringResource(R.string.edit))
                            }
                        }
                    }
                )
            }
        ) {
            Box(Modifier.padding(it)) {
                TagBrowserScreen(viewModel)
            }
        }
    }
}

@Composable
private fun RequestWebSearch(viewModel: TagBrowserViewModel, webSearchTool: WebSearchTool) {
    val context = LocalContext.current
    fun search(source: Source) {
        val intent = when (source) {
            Source.LastFm -> WebSearchLauncher.searchLastFmSong(context, viewModel.song.value)
            Source.MusicBrainz -> WebSearchLauncher.searchMusicBrainzSong(context, viewModel.song.value)
        }
        webSearchTool.launch(intent) {
            if (it != null) {
                viewModel.viewModelScope.launch(Dispatchers.Default) {
                    debug { Log.v("TagEditor", it.toString()) }
                    importResult(viewModel, it)
                }
            }
        }
    }

    fun onShowWikiDialog() {
        val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
        if (fragmentManager != null) {
            LastFmDialog.from(viewModel.song.value).show(fragmentManager, "WEB_SEARCH_DIALOG")
        }
    }
    RequestWebSearch(webSearchTool, ::search, ::onShowWikiDialog)
}

