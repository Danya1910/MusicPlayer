package com.example.musicplayer.data.di

import com.example.musicplayer.data.repositoryImpl.AuthRepositoryImpl
import com.example.musicplayer.domain.repository.AuthRepository
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

}