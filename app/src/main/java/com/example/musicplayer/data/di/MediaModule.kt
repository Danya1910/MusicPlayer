package com.example.musicplayer.data.di

import android.content.Context
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.repositoryImpl.MusicRepositoryImpl
import com.example.musicplayer.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        trackDao: TrackDao,
    ) : MusicRepository {
        return MusicRepositoryImpl(
            context = context,
            trackDao = trackDao,
        )
    }

}