package com.example.musicplayer.domain.model

data class PlayList (
    val id: String,
    val name: String,
    val updatedAt: Long,
    val imageUrl: String,
    val creatorId: String,
    val creatorName: String,
    val isPrivate: Boolean,
    val tracks: List<String>,
)