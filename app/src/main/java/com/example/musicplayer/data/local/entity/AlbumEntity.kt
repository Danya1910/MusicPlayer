package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "albums")
data class AlbumEntity (
    @PrimaryKey
    val albumId: String,
    val title: String = "",
    val artist: String = "",
    val imageUrl: String = "",
)