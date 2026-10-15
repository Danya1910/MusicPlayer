package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.Track


@Entity(tableName = "tracks_table")
data class TrackEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val imageUrl: String,
    val duration: Long,
    val isFavorite: Boolean,
    val localAudioPath: String? = null
)

fun TrackEntity.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        duration = duration,
        isFavorite = isFavorite,
        localAudioPath = localAudioPath
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artist = artist,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        duration = duration,
        isFavorite = isFavorite,
        localAudioPath = localAudioPath
    )
}