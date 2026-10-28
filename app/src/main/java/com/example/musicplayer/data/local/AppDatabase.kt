package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.local.entity.TrackEntity


@Database(
    entities = [TrackEntity::class, AlbumEntity::class, PlaylistEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun playListDao(): PlayListDao

}