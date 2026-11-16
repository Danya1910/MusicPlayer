package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.AlbumWithTracks
import com.example.musicplayer.data.local.entity.FavoriteAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Transaction
    @Query("SELECT * FROM albums")
    fun getAllAlbumsWithTracks(): Flow<List<AlbumWithTracks>>

    @Transaction
    @Query("SELECT * FROM albums")
    suspend fun getAllAlbumsWithTracksOnce(): List<AlbumWithTracks>

    @Transaction
    @Query("SELECT * FROM albums WHERE albumId = :albumId")
    fun getAlbumWithTracksById(albumId: String): Flow<AlbumWithTracks?>

    @Query("SELECT * FROM albums")
    suspend fun getAllAlbumsOnce(): List<AlbumEntity>

    @Delete
    suspend fun deleteAlbum(album: AlbumEntity)

    @Query("DELETE FROM albums")
    suspend fun clearAllAlbums()


    @Query("SELECT albumId FROM favorite_albums WHERE userId = :userId")
    fun observeFavoriteIds(userId: String): Flow<List<String>>

    @Query("SELECT albumId FROM favorite_albums WHERE userId = :userId")
    suspend fun getFavoriteIdsOnce(userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteAlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorites(favorites: List<FavoriteAlbumEntity>)

    @Query("DELETE FROM favorite_albums WHERE userId = :userId AND albumId = :albumId")
    suspend fun removeFavorite(userId: String, albumId: String)

    @Query("DELETE FROM favorite_albums WHERE userId = :userId")
    suspend fun clearFavoritesForUser(userId: String)

}