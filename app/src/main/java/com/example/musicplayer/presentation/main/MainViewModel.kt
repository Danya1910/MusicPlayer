package com.example.musicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.repository.TrackRepository
import com.example.musicplayer.presentation.main.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            trackRepository.getAllTrack()
                .catch { exception ->
                    _uiState.value = MainUiState.Error(
                        exception.localizedMessage ?: "Неизвестная ошибка"
                    )
                }
                .collect { tracks ->
                    _uiState.value = MainUiState.Success(tracks = tracks)
                }
        }
    }

}