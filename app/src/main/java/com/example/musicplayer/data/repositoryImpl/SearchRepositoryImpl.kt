package com.example.musicplayer.data.repositoryImpl

import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.SearchDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.wrapper.toDomain
import com.example.musicplayer.domain.model.SearchResult
import com.example.musicplayer.domain.model.SearchSuggestion
import com.example.musicplayer.domain.repository.SearchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val searchDao: SearchDao,
) : SearchRepository {

    override suspend fun suggest(query: String, limit: Int): List<SearchSuggestion> {
        val q = query.trim()
        if (q.length < 1) return emptyList()

        return coroutineScope {
            val artists = async { searchDao.suggestArtistsByPrefix(q, limit) }
            val tracks  = async { searchDao.suggestTracksByPrefix(q, limit) }
            val albums  = async { searchDao.suggestAlbumsByPrefix(q, limit) }

            (artists.await().map { SearchSuggestion.Artist(it) } +
                    tracks.await().map  { SearchSuggestion.Track(it) } +
                    albums.await().map  { SearchSuggestion.Album(it) })
                .distinctBy { it.value.lowercase() }
                .take(limit)
        }
    }

    override suspend fun search(query: String): List<SearchResult> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()

        return coroutineScope {
            val tracks = async { searchDao.searchTracks(q, 10) }
            val albums = async { searchDao.searchAlbums(q, 10) }

            tracks.await().map { SearchResult.TrackItem(it.toDomain()) } +
                    albums.await().map { SearchResult.AlbumItem(it.toDomain()) }
        }
    }

}