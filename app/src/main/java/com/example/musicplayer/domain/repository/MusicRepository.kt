package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {

    val currentTrack : StateFlow<Track?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>

    val isShuffleModeEnabled: StateFlow<Boolean>

    val currentQueue: StateFlow<List<Track>>

    fun playTrack(track: Track)

    fun playTrackWithQueue(track: Track, queue: List<Track>)

    fun togglePlayPause()

    fun seekTo(positionMs: Long)

    fun skipToNext()

    fun skipToPrevious()

    fun toggleShuffleMode()

}