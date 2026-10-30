package com.example.musicplayer.domain.model

data class PlayList(
    val id: String? = null,
    val name: String,
    val updatedAt: Long? = null,
    val imageUrl: String? = null,
    val creatorId: String? = null,
    val creatorName: String? = null,
    val isPrivate: Boolean = true,
    val tracks: List<String>? = emptyList(),
    val duration: String? = null,
)

