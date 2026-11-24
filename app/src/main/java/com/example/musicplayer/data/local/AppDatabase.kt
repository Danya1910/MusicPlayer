package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.LyricDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.SearchDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.FavoriteAlbumEntity
import com.example.musicplayer.data.local.entity.FavoriteTrackEntity
import com.example.musicplayer.data.local.entity.LyricsEntity
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.local.entity.TrackEntity


@Database(
    entities = [
        TrackEntity::class,
        AlbumEntity::class,
        PlaylistEntity::class,
        LyricsEntity::class,
        FavoriteTrackEntity::class,
        FavoriteAlbumEntity::class,
    ],
    version = 8,
    exportSchema = false,
)

@TypeConverters(PlaylistConverters::class, LyricsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun albumDao(): AlbumDao

    abstract fun playListDao(): PlayListDao

    abstract fun lyricsDao(): LyricDao

    abstract fun searchDao() : SearchDao

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

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Создаём таблицу избранного
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS favorite_tracks (
                userId TEXT NOT NULL,
                trackId TEXT NOT NULL,
                likedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, trackId)
            )
        """)

        // 2. Пересоздаём tracks_table без isFavorite,
        // строго соблюдая типы из TrackEntity
        db.execSQL("""
            CREATE TABLE tracks_table_new (
                id TEXT NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                audioUrl TEXT NOT NULL,
                imageUrl TEXT NOT NULL,
                duration INTEGER NOT NULL,
                genre TEXT NOT NULL,
                localAudioPath TEXT,
                albumName TEXT,
                albumId TEXT
            )
        """)

        // 3. Переносим данные
        db.execSQL("""
            INSERT INTO tracks_table_new (
                id, title, artist, audioUrl, imageUrl,
                duration, genre, localAudioPath, albumName, albumId
            )
            SELECT
                id, title, artist, audioUrl, imageUrl,
                duration, genre, localAudioPath, albumName, albumId
            FROM tracks_table
        """)

        // 4. Удаляем старую таблицу и переименовываем новую
        db.execSQL("DROP TABLE tracks_table")
        db.execSQL("ALTER TABLE tracks_table_new RENAME TO tracks_table")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Создаем таблицу избранных альбомов
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS favorite_albums (
                userId TEXT NOT NULL,
                albumId TEXT NOT NULL,
                likedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, albumId)
            )
        """)

        // 2. Пересоздаем таблицу albums без isFavorite
        // ВАЖНО: сверьтесь с вашим AlbumEntity, чтобы типы (TEXT, INTEGER, NOT NULL) совпадали!
        db.execSQL("""
            CREATE TABLE albums_new (
                albumId TEXT NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                imageUrl TEXT NOT NULL
            )
        """)

        // 3. Переносим данные
        db.execSQL("""
            INSERT INTO albums_new (albumId, title, artist, imageUrl)
            SELECT albumId, title, artist, imageUrl
            FROM albums
        """)

        // 4. Удаляем старую и переименовываем новую
        db.execSQL("DROP TABLE albums")
        db.execSQL("ALTER TABLE albums_new RENAME TO albums")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Индексы для tracks_table
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_tracks_table_artist` " +
                    "ON `tracks_table` (`artist`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_tracks_table_title` " +
                    "ON `tracks_table` (`title`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_tracks_table_artist_title` " +
                    "ON `tracks_table` (`artist`, `title`)"
        )

        // Индексы для albums
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_albums_artist` " +
                    "ON `albums` (`artist`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_albums_title` " +
                    "ON `albums` (`title`)"
        )
    }
}