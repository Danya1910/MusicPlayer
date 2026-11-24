package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.TrackEntity

@Dao
interface SearchDao {

    // ---------- Подсказки (suggest) ----------

    @Query("""
        SELECT DISTINCT artist AS value
        FROM tracks_table
        WHERE artist LIKE :q || '%'
        ORDER BY artist
        LIMIT :limit
    """)
    suspend fun suggestArtistsByPrefix(q: String, limit: Int): List<String>

    @Query("""
        SELECT DISTINCT title AS value
        FROM tracks_table
        WHERE title LIKE :q || '%'
        ORDER BY title
        LIMIT :limit
    """)
    suspend fun suggestTracksByPrefix(q: String, limit: Int): List<String>

    @Query("""
        SELECT DISTINCT title AS value
        FROM albums
        WHERE title LIKE :q || '%'
        ORDER BY title
        LIMIT :limit
    """)
    suspend fun suggestAlbumsByPrefix(q: String, limit: Int): List<String>

    // ---------- Полный поиск (в SQL, не в Kotlin) ----------

    @Query("""
        SELECT * FROM tracks_table
        WHERE title  LIKE '%' || :q || '%'
           OR artist LIKE '%' || :q || '%'
        LIMIT :limit
    """)
    suspend fun searchTracks(q: String, limit: Int): List<TrackEntity>

    @Query("""
        SELECT * FROM albums
        WHERE title  LIKE '%' || :q || '%'
           OR artist LIKE '%' || :q || '%'
        LIMIT :limit
    """)
    suspend fun searchAlbums(q: String, limit: Int): List<AlbumEntity>
}