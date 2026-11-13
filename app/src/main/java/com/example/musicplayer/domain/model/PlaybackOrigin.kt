package com.example.musicplayer.domain.model

sealed interface PlaybackOrigin {
    data class Album(val albumId: String) : PlaybackOrigin
    data class Playlist(val playlistId: String) : PlaybackOrigin
    data object Unknown: PlaybackOrigin
}