package com.example.musicplayer.presentation.main.util

import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track

data class PlaylistUi (
    val playlist: PlayList,
    val tracks: List<Track>,
)