/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.ui.modules.web

import player.phonograph.R
import player.phonograph.ui.compose.components.ListItem
import util.phonograph.tagsources.musicbrainz.MusicBrainzAction
import util.phonograph.tagsources.musicbrainz.MusicBrainzSearchResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import util.phonograph.tagsources.musicbrainz.MusicBrainzSearchResultArtists as SearchResultArtists
import util.phonograph.tagsources.musicbrainz.MusicBrainzSearchResultRecording as SearchResultRecording
import util.phonograph.tagsources.musicbrainz.MusicBrainzSearchResultReleases as SearchResultReleases
import util.phonograph.tagsources.musicbrainz.MusicBrainzSearchResultReleasesGroup as SearchResultReleasesGroup

@Composable
fun MusicBrainzSearchResult(
    result: MusicBrainzSearchResult?,
    modifier: Modifier = Modifier,
    onSelectItem: (MusicBrainzAction.View) -> Unit = {},
) {
    Box(
        modifier.fillMaxSize()
    ) {
        if (result != null && result.count > 0) {
            when (result) {
                is SearchResultReleasesGroup -> MusicBrainzSearchResultReleasesGroup(result) { onSelectItem(it) }
                is SearchResultReleases      -> MusicBrainzSearchResultReleases(result) { onSelectItem(it) }
                is SearchResultArtists       -> MusicBrainzSearchResultArtists(result) { onSelectItem(it) }
                is SearchResultRecording     -> MusicBrainzSearchResultRecording(result) { onSelectItem(it) }
            }
        } else {
            Text(
                stringResource(R.string.no_results),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 8.dp)
            )
        }
    }
}


@Composable
private fun <T> SearchResult(items: List<T>?, content: @Composable LazyItemScope.(T) -> Unit) {
    if (!items.isNullOrEmpty()) {
        LazyColumn {
            for (item in items) {
                item {
                    content(item)
                }
            }
        }
    }
}

@Composable
private fun MusicBrainzSearchResultReleasesGroup(
    result: SearchResultReleasesGroup,
    getDetail: (MusicBrainzAction.View) -> Unit,
) {
    val items = result.releasesGroup
    SearchResult(items) { releaseGroup ->
        val subtitle =
            releaseGroup.artistCredit.firstOrNull()?.name
                ?: releaseGroup.disambiguation
                ?: releaseGroup.firstReleaseDate
                ?: releaseGroup.releases?.firstOrNull()?.title
                ?: NA

        ListItem(Modifier, releaseGroup.title, subtitle, {
            getDetail(MusicBrainzAction.View(MusicBrainzAction.Target.ReleaseGroup, releaseGroup.id))
        }, {}, null)
    }
}
@Composable
private fun MusicBrainzSearchResultReleases(
    result: SearchResultReleases,
    getDetail: (MusicBrainzAction.View) -> Unit,
) {
    val items = result.releases
    SearchResult(items) { release ->
        val subtitle =
            release.artistCredit.firstOrNull()?.name
                ?: release.disambiguation
                ?: release.releaseGroup?.title
                ?: release.date
                ?: release.media.firstOrNull()?.format
                ?: NA
        ListItem(Modifier, release.title, subtitle, {
            getDetail(MusicBrainzAction.View(MusicBrainzAction.Target.Release, release.id))
        }, {}, null)
    }
}
@Composable
private fun MusicBrainzSearchResultArtists(
    result: SearchResultArtists,
    getDetail: (MusicBrainzAction.View) -> Unit,
) {
    val items = result.artists
    SearchResult(items) { artist ->
        val subtitle = artist.country
            ?: artist.area?.name
            ?: ""
        ListItem(Modifier, artist.name, subtitle, {
            getDetail(MusicBrainzAction.View(MusicBrainzAction.Target.Artist, artist.id))
        }, {}, null)
    }
}
@Composable
private fun MusicBrainzSearchResultRecording(
    result: SearchResultRecording,
    getDetail: (MusicBrainzAction.View) -> Unit,
) {
    val items = result.recordings
    SearchResult(items) { recording ->
        val subtitle =
            recording.artistCredit.firstOrNull()?.name
                ?: recording.disambiguation
                ?: recording.firstReleaseDate
                ?: recording.releases?.firstOrNull()?.title
                ?: NA
        ListItem(Modifier, recording.title, subtitle, {
            getDetail(MusicBrainzAction.View(MusicBrainzAction.Target.Recording, recording.id))
        }, {}, null)
    }
}

private const val NA = "-"
