package com.example.musicplayer.domain.model


sealed interface SearchResult {

    data class TrackItem(val track: Track) : SearchResult
    data class AlbumItem(val album: Album) : SearchResult

}