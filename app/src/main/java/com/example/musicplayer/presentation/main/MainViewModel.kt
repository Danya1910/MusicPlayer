package com.example.musicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.repository.TrackRepository
import com.example.musicplayer.presentation.main.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
) : ViewModel() {


    val uiState: StateFlow<MainUiState> = combine(
        trackRepository.getAllTrack(),
        trackRepository.getAlbums()
    ) { tracks, albums ->
        MainUiState.Success(tracks = tracks, albums = albums) as MainUiState
    }
        .catch { exception ->
            emit(MainUiState.Error(exception.localizedMessage ?: "Неизвестная ошибка"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )

}