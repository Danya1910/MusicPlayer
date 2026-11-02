package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks_table WHERE id = :trackId LIMIT 1")
    suspend fun getTrackById(trackId: String): TrackEntity?

    @Query("SELECT * FROM tracks_table")
    fun getAllTracksFlow() : Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracksOnce(): List<TrackEntity>

    @Query("UPDATE tracks_table SET isFavorite = :isFavorite WHERE id = :trackId")
    suspend fun updateFavoriteStatus(trackId: String, isFavorite: Boolean)

    @Query("UPDATE tracks_table SET localAudioPath = :localAudioPath WHERE id = :trackId")
    suspend fun updateAudioPath(trackId: String, localAudioPath: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("DELETE FROM tracks_table")
    suspend fun clearCache()

}