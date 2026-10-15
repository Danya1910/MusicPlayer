package com.example.musicplayer.presentation.main.state

import com.example.musicplayer.domain.model.Track

sealed interface MainUiState {

    data object Loading : MainUiState
    data class Success(val tracks: List<Track>) : MainUiState
    data class Error(val message: String) : MainUiState

}