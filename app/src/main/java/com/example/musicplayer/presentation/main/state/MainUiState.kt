package com.example.musicplayer.presentation.main.state

import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Track

sealed interface MainUiState {

    data object Loading : MainUiState
    data class Success(
        val tracks: List<Track>,
        val albums: List<Album>
    ) : MainUiState

    data class Error(val message: String) : MainUiState

}