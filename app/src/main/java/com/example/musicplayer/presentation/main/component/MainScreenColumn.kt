package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.White


@Composable
fun MainScreenColumn(
    tracks: List<Track>,
    albums: List<Album>,
    onTrackClick: (Track) -> Unit,
    onAlbumClick: (Album) -> Unit,
    playerState: PlayerUiState,
    onMoreAboutTrack: (Track) -> Unit,
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        item {
            Text(
                text = "Слушать плейлисты",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            AlbumsList(
                albums = albums,
                onAlbumClick = { album ->
                    onAlbumClick(album)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Text(
                text = "Слушать музыку",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        itemsIndexed(
            items = tracks,
            key = { index, track -> "${track.id}_$index" }
        ) { index, track ->
            TrackItem(
                track = track,
                onTrackClick = {
                    onTrackClick(track)
                },
                onMoreClick = {
                    onMoreAboutTrack(track)
                }
            )
        }
    }
}