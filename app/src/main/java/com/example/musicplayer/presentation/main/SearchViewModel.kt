package com.example.musicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.SearchResult
import com.example.musicplayer.domain.model.SearchSuggestion
import com.example.musicplayer.domain.repository.SearchRepository
import com.example.musicplayer.presentation.main.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val suggestions: StateFlow<List<SearchSuggestion>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            flow { emit(searchRepository.suggest(q, limit = 8)) }
                .catch { emit(emptyList()) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val results: StateFlow<SearchUiState> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest<String, SearchUiState> { query ->
            if (query.isBlank()) {
                flowOf(SearchUiState.Empty)
            } else {
                flow {
                    emit(SearchUiState.Loading)
                    runCatching { searchRepository.search(query = query) }
                        .onSuccess { emit(SearchUiState.Success(it)) }
                        .onFailure { emit(SearchUiState.Error(it.message)) }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState.Empty
        )


    val topTracks: StateFlow<List<SearchResult.TrackItem>> = results
        .map { state ->
            (state as? SearchUiState.Success)
                ?.results
                ?.filterIsInstance<SearchResult.TrackItem>()
                ?.take(7)
                ?: emptyList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val topAlbums: StateFlow<List<SearchResult.AlbumItem>> = results
        .map { state ->
            (state as? SearchUiState.Success)
                ?.results
                ?.filterIsInstance<SearchResult.AlbumItem>()
                ?.take(4)
                ?: emptyList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun onQueryChanged(
        query: String
    ) {
        _query.value = query
    }

}