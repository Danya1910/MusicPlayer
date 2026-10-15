package com.example.musicplayer.data.dto

data class TrackDto (
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val imageUrl: String,
    val duration: Long,
    val isFavorite: Boolean,
    val localAudioPath: String? = null,
)