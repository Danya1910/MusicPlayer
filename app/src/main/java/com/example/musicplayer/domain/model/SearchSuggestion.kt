package com.example.musicplayer.domain.model

sealed interface SearchSuggestion {
    val value: String
    data class Artist(override val value: String) : SearchSuggestion
    data class Track(override val value: String)  : SearchSuggestion
    data class Album(override val value: String)  : SearchSuggestion
}