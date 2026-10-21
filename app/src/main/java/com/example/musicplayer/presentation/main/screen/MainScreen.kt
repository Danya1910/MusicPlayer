package com.example.musicplayer.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.BottomBar
import com.example.musicplayer.presentation.main.component.MainScreenColumn
import com.example.musicplayer.presentation.main.component.NowPlayingBar
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
) {

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    val currentTrack by mainViewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by mainViewModel.isPlaying.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DeepBlack,
        bottomBar = {
            BottomBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = DeepBlack
                )
                .padding(paddingValues)
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
                            modifier = Modifier.fillMaxSize()
                        ) {
                            MainScreenColumn(
                                tracks = state.tracks,
                                albums = state.albums,
                                onTrackClick = { track ->
                                    mainViewModel.playTrack(track = track)
                                },
                                onAlbumClick = {
                                    //навигация на страницу альбома
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

            currentTrack?.let { track ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp)
                ) {
                    NowPlayingBar(
                        track = track,
                        onPlayPauseClick = {
                            mainViewModel.togglePlayPause()
                        },
                        onCardClick = {
                            //navigate to full screen player
                        },
                        isPlaying = isPlaying,
                    )
                }
            }
        }
    }

}