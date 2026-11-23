package com.example.musicplayer.data.repositoryImpl

import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.SearchRepository
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
): SearchRepository {

    override suspend fun search(query: String): List<Track> {

        val cachedTracks = trackDao.getAllTracksOnce()

        val tracksToReturn: List<Track>

        cachedTracks.forEach { track ->
            if(track.title == query && track.artist == query){
            }
        }
        return emptyList()

    }

}