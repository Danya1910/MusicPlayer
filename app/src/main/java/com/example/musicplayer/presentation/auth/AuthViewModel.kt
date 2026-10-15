package com.example.musicplayer.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.presentation.auth.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: Lazy<AuthRepository>,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun signIn(
        email: String,
        password: String,
    ) {
        if (email.isBlank() && password.isBlank()) {
            _uiState.value = AuthUiState.Error("Поля не могут быть пустыми")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            authRepository.get().signIn(
                email = email,
                password = password
            )
                .onSuccess {
                    _uiState.value = AuthUiState.Success
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(
                        exception.localizedMessage ?: "Неизвестная ошибка"
                    )
                }
        }
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
    ) {
        if(email.isBlank() && password.isBlank() && name.isBlank()) {
            _uiState.value = AuthUiState.Error("Поля не могут быть пустыми")
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            authRepository.get().signUp(
                email = email,
                password = password,
                name = name,
            )
                .onSuccess {
                    _uiState.value = AuthUiState.Success
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(
                        exception.localizedMessage ?: "Неизвестная ошибка"
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

}