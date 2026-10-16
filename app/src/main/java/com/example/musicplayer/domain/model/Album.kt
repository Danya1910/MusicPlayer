package com.example.musicplayer.domain.model

data class Album (
    val id: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val tracks: List<Track>,
)