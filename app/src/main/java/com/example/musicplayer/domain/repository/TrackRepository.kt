package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getAllTrack(): Flow<List<Track>>

}