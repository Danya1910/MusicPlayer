package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getAllTrack(): Flow<List<Track>>

    fun getAlbums() : Flow<List<Album>>

    suspend fun createPlayList(
        title: String,
    ) : String

    fun getUserPlayLists() : Flow<List<PlayList>>

    suspend fun addTrackToPlayList(playListId: String, trackId: String)

    suspend fun removeTrackFromPlayList(playListId: String, trackId: String)

}