package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.PlayList

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val updatedAt: Long?,
    val imageUrl: String?,
    val creatorId: String?,
    val creatorName: String?,
    val isPrivate: Boolean,
    val tracks: List<String> // Конвертер сам превратит это в строку в БД
)

fun PlaylistEntity.toDomain(): PlayList {
    return PlayList(
        id = id,
        name = name,
        updatedAt = updatedAt,
        imageUrl = imageUrl,
        creatorId = creatorId,
        creatorName = creatorName,
        isPrivate = isPrivate,
        tracks = tracks
    )
}

fun PlayList.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = id ?: "",
        name = name,
        updatedAt = updatedAt,
        imageUrl = imageUrl,
        creatorId = creatorId,
        creatorName = creatorName,
        isPrivate = isPrivate,
        tracks = tracks ?: emptyList()
    )
}