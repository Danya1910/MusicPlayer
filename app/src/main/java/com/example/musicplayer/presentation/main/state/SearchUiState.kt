package com.example.musicplayer.presentation.main.state

import com.example.musicplayer.domain.model.Track

sealed interface SearchUiState {
    data object Empty: SearchUiState
    data object Loading: SearchUiState
    data class Success(val results: List<Track>): SearchUiState
    data class Error(val error: String?): SearchUiState
}