package com.example.musicplayer.presentation.splash

import androidx.lifecycle.ViewModel
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        authCheck()
    }

    private fun authCheck() {
        if(authRepository.isUserAuthenticated()) {
            _startDestination.value = Screen.MainGraph
        } else{
            _startDestination.value = Screen.AuthGraph
        }
    }

}