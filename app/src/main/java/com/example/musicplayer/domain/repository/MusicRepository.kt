package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.PlaybackOrigin
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {

    val currentTrackId: StateFlow<String?>
    val currentTrack: StateFlow<Track?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>

    val isShuffleModeEnabled: StateFlow<Boolean>

    val currentQueue: StateFlow<List<Track>>

    val repeatMode: StateFlow<Int>

    val sleepTimerRemainingSec: StateFlow<Long?>

    val stopAtTrackEnd: StateFlow<Boolean>

    val playbackOrigin: StateFlow<PlaybackOrigin>

    fun playTrack(
        track: Track,
        playbackOrigin: PlaybackOrigin,
    )

    fun playTrackWithQueue(
        track: Track,
        queue: List<Track>,
        playbackOrigin: PlaybackOrigin,
    )

    fun togglePlayPause()

    fun seekTo(positionMs: Long)

    fun skipToNext()

    fun skipToPrevious()

    fun toggleShuffleMode()

    fun toggleRepeatMode()

    fun addTrackToPlayNext(track: Track)

    fun moveTrackInQueue(fromUpcomingIndex: Int, toUpcomingIndex: Int)

    fun removeTrackFromQueue(index: Int)

    fun updateFavoriteStatus(trackId: String, isFavorite: Boolean)

    fun setSleepTimer(minutes: Int)

    fun setSleepTimerToTrackEnd()

    fun cancelSleepTimer()

}