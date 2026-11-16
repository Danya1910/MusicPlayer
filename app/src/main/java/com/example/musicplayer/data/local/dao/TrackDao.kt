package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.data.local.entity.FavoriteTrackEntity
import com.example.musicplayer.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks_table WHERE id = :trackId LIMIT 1")
    suspend fun getTrackById(trackId: String): TrackEntity?

    @Query("SELECT * FROM tracks_table WHERE id = :trackId LIMIT 1")
    fun observeTrackById(trackId: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks_table")
    fun getAllTracksFlow() : Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracksOnce(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_tracks WHERE userId = :userId")
    fun observeFavoriteIds(userId: String): Flow<List<String>>

    @Query("SELECT trackId FROM favorite_tracks WHERE userId = :userId")
    suspend fun getFavoriteIdsOnce(userId: String): List<String>


    @Query("SELECT * FROM favorite_tracks WHERE userId = :userId ORDER BY likedAt DESC")
    fun observeFavorites(userId: String): Flow<List<FavoriteTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorites(favorites: List<FavoriteTrackEntity>)

    @Query("DELETE FROM favorite_tracks WHERE userId = :userId AND trackId = :trackId")
    suspend fun removeFavorite(userId: String, trackId: String)

    @Query("DELETE FROM favorite_tracks WHERE userId = :userId")
    suspend fun clearFavoritesForUser(userId: String)


    @Query("UPDATE tracks_table SET localAudioPath = :localAudioPath WHERE id = :trackId")
    suspend fun updateAudioPath(trackId: String, localAudioPath: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Query("DELETE FROM tracks_table")
    suspend fun clearCache()

}