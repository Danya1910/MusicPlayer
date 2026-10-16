package com.example.musicplayer.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation


data class AlbumWithTracks (
    @Embedded
    val album: AlbumEntity,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
    )
    val tracks: List<TrackEntity>
)