package com.example.musicplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signIn(email: String, password: String) : Result<Unit>

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
    ) : Result<Unit>

    fun logOut()

    fun isUserAuthenticated(): Boolean

    fun authStateFlow(): Flow<Boolean>

}