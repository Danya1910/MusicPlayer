package com.example.musicplayer.data.wrapper

import com.example.musicplayer.data.local.entity.AlbumWithTracks
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.domain.model.Album

fun AlbumWithTracks.toDomain() : Album {
    return Album(
        id = album.albumId,
        title = album.title,
        artist = album.artist,
        imageUrl = album.imageUrl,
        tracks = this.tracks.map { it.toDomain() }
    )
}