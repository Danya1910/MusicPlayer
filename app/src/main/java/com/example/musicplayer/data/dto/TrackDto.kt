package com.example.musicplayer.data.dto

data class TrackDto (
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val audioUrl: String = "",
    val imageUrl: String = "",
    val duration: Long = 0L,
    val genre: String = "",
    val isFavorite: Boolean = false,
    val localAudioPath: String? = null,
    val albumName: String? = null,
    val albumId: String? = null
)