package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.Album


@Entity(
    tableName = "albums",
    indices = [
        Index(value = ["artist"]),
        Index(value = ["title"])
    ]
)
data class AlbumEntity (
    @PrimaryKey
    val albumId: String,
    val title: String = "",
    val artist: String = "",
    val imageUrl: String = "",
    val artistImageUrl: String = "",
)

fun AlbumEntity.toDomain(): Album = Album(
    id = albumId,
    title = title,
    artist = artist,
    imageUrl = imageUrl,
    isFavorite = false,
    artistImageUrl = artistImageUrl,
    tracks = emptyList(),
)

@Entity(
    tableName = "favorite_albums",
    primaryKeys = ["userId", "albumId"],
)
data class FavoriteAlbumEntity(
    val userId: String,
    val albumId: String,
    val likedAt: Long,
)