package com.example.musicplayer.presentation.player

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _paletteState = MutableStateFlow<Map<String, Triple<Color, Color, Color>>>(emptyMap())

    val uiState: StateFlow<PlayerUiState> = combine(
        musicRepository.currentTrack,
        musicRepository.isPlaying,
        musicRepository.currentPosition,
        musicRepository.duration,
        _paletteState
    ) { track, isPlaying, position, duration, paletteMap ->

        val colors = if (track != null) {
            paletteMap[track.id] ?: Triple(DeepBlack, White, Gray)
        } else {
            Triple(DeepBlack, White, Gray)
        }

        PlayerUiState(
            currentTrack = track,
            isPlaying = isPlaying,
            currentPosition = position,
            duration = duration,
            bgColor = colors.first,
            titleColor = colors.second,
            bodyColor = colors.third
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState()
    )

    fun togglePlayPause() {
        musicRepository.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        musicRepository.seekTo(positionMs = positionMs)
    }

    fun updateColorsFromBitmap(trackId: String, bitmap: Bitmap) {
        if (_paletteState.value.containsKey(trackId)) return

        viewModelScope.launch(Dispatchers.Default) {
            Palette.from(bitmap).generate { palette ->
                val swatch = palette?.darkVibrantSwatch
                    ?: palette?.darkMutedSwatch
                    ?: palette?.dominantSwatch

                if (swatch != null) {
                    val newColors = Triple(
                        Color(swatch.rgb),
                        Color(swatch.titleTextColor),
                        Color(swatch.bodyTextColor)
                    )
                    _paletteState.value = _paletteState.value + (trackId to newColors)
                }
            }
        }
    }


}