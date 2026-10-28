package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlayListDao {

    @Query("SELECT * FROM playlists_table WHERE creatorId = :userId")
    suspend fun getPlaylistsOnce(userId: String): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE creatorId = :userId ORDER BY updatedAt DESC")
    fun getPlaylistsFlow(userId: String): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Query("DELETE FROM playlists_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: String)

}