package com.example.musicplayer.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.repository.AuthRepository
import com.example.musicplayer.presentation.auth.state.AuthUiState
import com.example.musicplayer.presentation.auth.util.AuthToastData
import com.example.musicplayer.presentation.main.util.ToastType
import com.google.firebase.auth.FirebaseAuthException
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

    private val _toast = MutableStateFlow<AuthToastData?>(null)
    val toast = _toast.asStateFlow()

    fun signIn(
        email: String,
        password: String,
    ) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
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
                        exception.toUserMessage()
                    )
                    _toast.value= AuthToastData(
                        title = "Error",
                        message = exception.toUserMessage(),
                        type = ToastType.ERROR,
                    )
                }
        }
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
    ) {
        if (email.isBlank() && password.isBlank() && name.isBlank()) {
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
                        exception.toUserMessage()
                    )
                    _toast.value= AuthToastData(
                        title = "Error",
                        message = exception.toUserMessage(),
                        type = ToastType.ERROR,
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
        _toast.value = null
    }

}

private fun Throwable.toUserMessage(): String = when ((this as? FirebaseAuthException)?.errorCode) {
    "ERROR_INVALID_CREDENTIAL" -> "Wrong email or password"
    "ERROR_USER_NOT_FOUND" -> "The user with this email was not found"
    "ERROR_WRONG_PASSWORD" -> "Wrong password"
    "ERROR_INVALID_EMAIL" -> "Invalid email"
    "ERROR_USER_DISABLED" -> "Account disabled"
    "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Try later"
    "ERROR_NETWORK_REQUEST_FAILED" -> "Check network connection"
    else -> {
        Log.w("AuthViewModel", "Необработанный код ошибки: ${(this as? FirebaseAuthException)?.errorCode}")
        "Unknown error"
    }
}