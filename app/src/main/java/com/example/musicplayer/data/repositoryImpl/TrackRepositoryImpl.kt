package com.example.musicplayer.data.repositoryImpl

import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.local.entity.toEntity
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.TrackRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class TrackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val trackDao: TrackDao,
) : TrackRepository {

    override fun getAllTrack(): Flow<List<Track>> = flow {

        val savedTracks = trackDao.getAllTracksOnce().map { it.toDomain() }
        emit(savedTracks)

        try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: throw Exception(
                "Пользователь не авторизирован"
            )
            val favoriteTrackIds = firestore.collection("users")
                .document(currentUserId)
                .collection("favorites")
                .get()
                .await()
                .map { it.id }

            val allTracksSnapshot = firestore.collection("tracks").get().await()

            val remoteTracks = allTracksSnapshot.documents.map { document ->
                val trackId = document.id
                Track(
                    id = trackId,
                    title = document.getString("title") ?: "",
                    artist = document.getString("artist") ?: "",
                    audioUrl = document.getString("audioUrl") ?: "",
                    imageUrl = document.getString("coverUrl") ?: "",
                    duration = document.getLong("duration") ?: 0L,
                    isFavorite = favoriteTrackIds.contains(trackId),
                    localAudioPath = null
                )
            }
            val entitiesToInsert = remoteTracks.map { remoteTrack ->
                val cachedTrack = trackDao.getAllTracksOnce().find { it.id == remoteTrack.id }
                remoteTrack.toEntity().copy(
                    localAudioPath = cachedTrack?.localAudioPath
                )
            }
            trackDao.insertTracks(entitiesToInsert)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        emitAll(trackDao.getAllTracksFlow().map { list -> list.map { it.toDomain() } })

    }.flowOn(Dispatchers.IO)

}