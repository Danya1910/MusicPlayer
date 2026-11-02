package com.example.musicplayer.data.di

import com.example.musicplayer.data.repositoryImpl.AuthRepositoryImpl
import com.example.musicplayer.data.repositoryImpl.FileRepositoryImpl
import com.example.musicplayer.data.repositoryImpl.TrackRepositoryImpl
import com.example.musicplayer.data.repositoryImpl.UserRepositoryImpl
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.domain.repository.FileRepository
import com.example.musicplayer.domain.repository.TrackRepository
import com.example.musicplayer.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
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
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ) : UserRepository

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        fileRepository: FileRepositoryImpl
    ) : FileRepository


}