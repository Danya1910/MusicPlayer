package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.LyricDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.LyricsEntity
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.local.entity.TrackEntity


@Database(
    entities = [
        TrackEntity::class,
        AlbumEntity::class,
        PlaylistEntity::class,
        LyricsEntity::class,
    ],
    version = 5,
    exportSchema = false,
)

@TypeConverters(PlaylistConverters::class, LyricsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun playListDao(): PlayListDao

    abstract fun lyricsDao(): LyricDao

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Создаем таблицу плейлистов, строго соответствующую схеме Room
        db.execSQL(
            """
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
        """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем колонку duration в таблицу playlists_table
        // Тип TEXT, так как в сущности это String?, и он может быть NULL
        db.execSQL("ALTER TABLE playlists_table ADD COLUMN duration TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lyrics (
                trackId TEXT NOT NULL PRIMARY KEY,
                lines TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE albums
            ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }
}