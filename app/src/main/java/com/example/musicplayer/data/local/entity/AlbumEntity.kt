package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.Album


@Entity(tableName = "albums")
data class AlbumEntity (
    @PrimaryKey
    val albumId: String,
    val title: String = "",
    val artist: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
)

fun AlbumEntity.toDomain(): Album = Album(
    id = albumId,
    title = title,
    artist = artist,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
    tracks = emptyList(),
)