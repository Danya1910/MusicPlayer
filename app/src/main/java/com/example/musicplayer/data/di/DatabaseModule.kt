package com.example.musicplayer.data.di

import android.content.Context
import androidx.room.Room
import com.example.musicplayer.data.local.AppDatabase
import com.example.musicplayer.data.local.MIGRATION_1_2
import com.example.musicplayer.data.local.MIGRATION_2_3
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "music_player_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    }

    @Provides
    @Singleton
    fun provideTrackDao(database: AppDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    @Singleton
    fun provideAlbumDao(database: AppDatabase): AlbumDao {
        return database.albumDao()
    }

    @Provides
    @Singleton
    fun providePlayListDao(database: AppDatabase): PlayListDao {
        return database.playListDao()
    }

}