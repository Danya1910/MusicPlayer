package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.SearchResult
import com.example.musicplayer.domain.model.SearchSuggestion

interface SearchRepository {

    suspend fun suggest(query: String, limit: Int) : List<SearchSuggestion>

    suspend fun search(query: String) : List<SearchResult>

}