package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserData(): Flow<User>

}