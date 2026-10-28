package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@TypeConverters(PlaylistConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun playListDao(): PlayListDao

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Создаем таблицу плейлистов, строго соответствующую схеме Room
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `playlists_table` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `updatedAt` INTEGER, 
                `imageUrl` TEXT, 
                `creatorId` TEXT, 
                `creatorName` TEXT, 
                `isPrivate` INTEGER NOT NULL, 
                `tracks` TEXT NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """.trimIndent())
    }
}