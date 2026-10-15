package com.example.musicplayer.domain.model

data class Track (
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val imageUrl: String,
    val duration: Long,
    val isFavorite: Boolean,
    val localAudioPath: String? = null,
)