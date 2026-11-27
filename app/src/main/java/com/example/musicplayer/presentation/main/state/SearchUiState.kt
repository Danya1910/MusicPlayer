package com.example.musicplayer.presentation.main.state

import com.example.musicplayer.domain.model.SearchResult

sealed interface SearchUiState {
    data object Empty: SearchUiState
    data object Loading: SearchUiState
    data class Success(val results: List<SearchResult>): SearchUiState
    data class Error(val error: String?): SearchUiState
}