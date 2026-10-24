package com.example.musicplayer.presentation.player.state

import androidx.compose.ui.graphics.Color
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White

data class PlayerUiState (
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,

    val bgColor: Color = DeepBlack,
    val titleColor: Color = White,
    val bodyColor: Color = Gray,

    val paletteState: Map<String, Triple<Color, Color, Color>> = emptyMap(),

    val isShuffleEnabled: Boolean = false,
)