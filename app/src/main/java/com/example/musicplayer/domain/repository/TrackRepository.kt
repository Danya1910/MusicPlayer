package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.LyricLine
import com.example.musicplayer.domain.model.Lyrics
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getAllTrack(): Flow<List<Track>>

    fun getAlbums(): Flow<List<Album>>

    suspend fun getLyrics(trackId: String): Lyrics?

    suspend fun createPlayList(
        title: String,
        imageUrl: String,
    ): String

    fun getUserPlayLists(): Flow<List<PlayList>>

    suspend fun addTrackToPlayList(playListId: String, trackId: String)

    suspend fun removeTrackFromPlayList(playListId: String, trackId: String)

    suspend fun likeTrack(trackId: String)

    suspend fun unlikeTrack(trackId: String)

    suspend fun likeAlbum(albumId: String)

    suspend fun unlikeAlbum(albumId: String)

}