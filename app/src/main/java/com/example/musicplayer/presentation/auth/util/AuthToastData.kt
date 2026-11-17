package com.example.musicplayer.presentation.auth.util

import com.example.musicplayer.presentation.main.util.ToastType


data class AuthToastData(
    val title: String,
    val message: String? = null,
    val type: ToastType = ToastType.INFO,
)