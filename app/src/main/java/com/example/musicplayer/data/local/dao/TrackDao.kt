package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.data.local.entity.TrackEntity
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks_table")
    fun getAllTracksFlow() : Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracksOnce(): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Update
    suspend fun updateTrack(track: Track)

    @Query("DELETE FROM tracks_table")
    suspend fun clearCache()

}