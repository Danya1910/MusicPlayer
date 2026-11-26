package com.example.musicplayer.domain.model

sealed interface PlaybackOrigin {
    data class Album(val albumId: String, val albumTitle: String) : PlaybackOrigin
    data class Playlist(val playlistId: String, val playlistTitle: String) : PlaybackOrigin
    data class Mix(val genre: String) : PlaybackOrigin
    data object Unknown: PlaybackOrigin
}