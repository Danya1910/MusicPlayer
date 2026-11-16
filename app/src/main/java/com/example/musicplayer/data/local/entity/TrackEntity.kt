package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.Track


@Entity(tableName = "tracks_table")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val imageUrl: String,
    val duration: Long,
    val genre: String = "",
    val localAudioPath: String?,
    val albumName: String?,
    val albumId: String?,
)

fun TrackEntity.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        duration = duration,
        genre = genre,
        localAudioPath = localAudioPath,
        albumName = albumName,
        albumId = albumId,
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
        genre = genre,
        localAudioPath = localAudioPath,
        albumName = albumName,
        albumId = albumId,
    )
}

@Entity(
    tableName = "favorite_tracks",
    primaryKeys = ["userId", "trackId"],
)
data class FavoriteTrackEntity(
    val userId: String,
    val trackId: String,
    val likedAt: Long,
)

