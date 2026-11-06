package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.local.entity.LyricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricDao {

    @Query("SELECT * FROM lyrics WHERE trackId =:trackId")
    fun getLyricsOnce(trackId: String): LyricsEntity?

    @Query("SELECT * FROM lyrics WHERE trackId =:trackId")
    fun getLyrics(trackId: String): Flow<LyricsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(lyrics: LyricsEntity)
}