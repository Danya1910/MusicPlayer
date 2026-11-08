package com.example.musicplayer.presentation.main.util

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

data class ToastData(
    val title: String,
    val message: String? = null,
    val type: ToastType = ToastType.INFO,
)