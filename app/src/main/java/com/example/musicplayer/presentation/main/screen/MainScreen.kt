package com.example.musicplayer.presentation.main.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.AlbumsList
import com.example.musicplayer.presentation.main.component.TracksList
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
) {

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    Log.d("MainScreen", "OPEN")

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
                    AlbumsList(albums = state.albums, onAlbumClick = {})
                    Spacer(modifier = Modifier.height(20.dp))
                    TracksList(tracks = state.tracks)
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
    }

}