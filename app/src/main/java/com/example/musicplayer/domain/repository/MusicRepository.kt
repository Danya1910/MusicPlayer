package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {

    val currentTrack : StateFlow<Track?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>

    fun playTrack(track: Track)

    fun togglePlayPause()

    fun seekTo(positionMs: Long)

}