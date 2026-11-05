package com.example.musicplayer.presentation.main.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.AlbumView
import com.example.musicplayer.presentation.main.component.MainScreenColumn
import com.example.musicplayer.presentation.main.component.ModalRightSheet
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTest(
    mainViewModel: MainViewModel,
    navController: NavController,
    onMoreClick: (Track) -> Unit,

    ) {

    BackHandler(enabled = true) {}


    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()


    var isAlbumViewVisible by remember { mutableStateOf(false) }

    var clickedAlbum by remember { mutableStateOf<Album?>(null) }


    val playerState by mainViewModel.playerState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack
            )
    ) {
        when (val state = uiState) {
            is MainUiState.Loading ->
                Text(
                    textAlign = TextAlign.Center,
                    text = "Загрузка нах...",
                    fontSize = 23.sp,
                    color = White,
                    modifier = Modifier.fillMaxSize()
                )

            is MainUiState.Success ->
                if (state.tracks.isEmpty() && state.albums.isEmpty()) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Списки пустые. В базе данных ничего нет!",
                        fontSize = 18.sp,
                        color = White,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        MainScreenColumn(
                            tracks = state.tracks,
                            albums = state.albums,
                            onTrackClick = { track ->
                                mainViewModel.playTrackWithQueue(
                                    track = track,
                                    queue = state.tracks,
                                )
                            },
                            onAlbumClick = { album ->
                                clickedAlbum = album
                                isAlbumViewVisible = true
                            },
                            playerState = playerState,
                            onMoreAboutTrack = { track ->
                                onMoreClick(track)
                            },
                            toggleFavorite = { track ->
                                mainViewModel.toggleFavorite(track)
                            },
                            onDownloadClick = { track ->
                                mainViewModel.onDownloadClick(track)
                            }
                        )

                    }
                }

            is MainUiState.Error ->
                Text(
                    textAlign = TextAlign.Center,
                    text = "Ошибочка: ${state.message}",
                    fontSize = 23.sp,
                    color = White,
                    modifier = Modifier.fillMaxSize()
                )
        }
        if (isAlbumViewVisible) {
            ModalRightSheet(
                onDismissRequest = {
                    isAlbumViewVisible = false
                    clickedAlbum = null
                },
                content = {
                    clickedAlbum?.let { album ->
                        AlbumView(
                            album = album,
                            onHeartClick = {

                            },
                            onCancelClick = {
                                isAlbumViewVisible = false
                                clickedAlbum = null
                            },
                            onPlayPauseClick = { tracks ->
                                tracks.firstOrNull()?.let { firstTrack ->
                                    mainViewModel.playTrackWithQueue(
                                        track = firstTrack,
                                        queue = tracks,
                                    )
                                }
                            },
                            onDownloadAlbumClick = {

                            },
                            onBitmapLoaded = { bitmap ->
                                mainViewModel.updateColorsFromBitmap(
                                    id = album.id,
                                    bitmap = bitmap,
                                )
                            },
                            playerState = playerState,
                            onMoreClick = { track ->
                                onMoreClick(track)
                            }
                        )
                    }
                }
            )
        }
    }
}