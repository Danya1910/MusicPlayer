package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Track

interface SearchRepository {

    suspend fun search(query: String) : List<Track>

}