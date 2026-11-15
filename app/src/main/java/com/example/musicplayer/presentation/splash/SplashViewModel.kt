package com.example.musicplayer.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination = _startDestination.asStateFlow()



    init {
        viewModelScope.launch {
            authRepository.authStateFlow().collect { isAuthenticated ->
                _startDestination.value = if (isAuthenticated) {
                    Screen.MainGraph
                } else {
                    Screen.AuthGraph
                }
            }
        }
    }

}