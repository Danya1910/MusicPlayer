package com.example.musicplayer.data.di

import com.example.musicplayer.data.repositoryImpl.AuthRepositoryImpl
import com.example.musicplayer.data.repositoryImpl.MusicRepositoryImpl
import com.example.musicplayer.data.repositoryImpl.TrackRepositoryImpl
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindTrackRepository(
        trackRepository: TrackRepositoryImpl
    ) : TrackRepository

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepository: MusicRepositoryImpl
    ): MusicRepository

}