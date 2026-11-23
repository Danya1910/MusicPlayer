package com.example.musicplayer.data.repositoryImpl

import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.wrapper.toDomain
import com.example.musicplayer.domain.model.SearchResult
import com.example.musicplayer.domain.repository.SearchRepository
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
) : SearchRepository {

    override suspend fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()

        val tracks = trackDao.getAllTracksOnce()
            .filter { trackEntity ->
                trackEntity.title.lowercase().contains(q) ||
                trackEntity.artist.lowercase().contains(q)
            }.map {
                SearchResult.TrackItem(it.toDomain())
            }

        val albums = albumDao.getAllAlbumsWithTracksOnce()
            .filter { albumEntity ->
                albumEntity.album.artist.contains(q) ||
                        albumEntity.album.title.contains(q)
            }.map {
                SearchResult.AlbumItem(it.toDomain())
            }

        return tracks + albums

    }

}