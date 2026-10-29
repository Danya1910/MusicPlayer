package com.example.musicplayer.data.repositoryImpl

import android.util.Log
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.local.entity.toEntity
import com.example.musicplayer.data.wrapper.toDomain
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.TrackRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    private val albumDao: AlbumDao,
    private val playListDao: PlayListDao,
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
                    imageUrl = document.getString("imageUrl") ?: "",
                    duration = document.getLong("duration") ?: 0L,
                    genre = document.getString("genre") ?: "",
                    isFavorite = favoriteTrackIds.contains(trackId),
                    localAudioPath = null,
                    albumName = document.getString("albumName") ?: "",
                    albumId = document.getString("albumId") ?: "",
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

    override fun getAlbums(): Flow<List<Album>> = flow {

        val cachedAlbums = albumDao.getAllAlbumsWithTracksOnce().map { it.toDomain() }
        emit(cachedAlbums)

        try {
            if (firebaseAuth.currentUser == null) throw Exception(
                "Пользователь не авторизирован"
            )

            val allAlbumsSnapshot = firestore.collection("albums").get().await()


            val remoteAlbumsEntities = allAlbumsSnapshot.documents.map { document ->
                AlbumEntity(
                    albumId = document.id,
                    title = document.getString("title") ?: "Без названия",
                    artist = document.getString("artist") ?: "Неизвестный исполнитель",
                    imageUrl = document.getString("imageUrl") ?: ""
                )
            }

            Log.d("Album", remoteAlbumsEntities.toString())
            albumDao.insertAlbums(remoteAlbumsEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createPlayList(title: String): String {

        val currentUser = firebaseAuth.currentUser

        val playList = PlayList(
            name = title,
            updatedAt = System.currentTimeMillis(),
            creatorId = currentUser?.uid,
            creatorName = currentUser?.displayName ?: "Unknown User",
        )

        val newDocumentRef = firestore
            .collection("playlists")
            .document()

        val generatedId = newDocumentRef.id

        val finalPlayList = playList.copy(id = generatedId)

        try {
            newDocumentRef.set(finalPlayList).await()
            return generatedId
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getUserPlayLists(): Flow<List<PlayList>> = callbackFlow {
        val currentUserId = firebaseAuth.currentUser?.uid
            ?: throw Exception("Пользователь не авторизирован")

        val savedPlaylists = playListDao.getPlaylistsOnce(currentUserId).map { it.toDomain() }
        send(savedPlaylists)

        val query = firestore.collection("playlists")
            .whereEqualTo("creatorId", currentUserId)

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val remotePlaylists = snapshot.documents.map { document ->
                    val rawTracks = document.get("tracks") as? List<*>
                    val trackIds = rawTracks?.mapNotNull { it?.toString() } ?: emptyList()

                    PlayList(
                        id = document.id,
                        name = document.getString("name") ?: "Без названия",
                        updatedAt = document.getLong("updatedAt") ?: 0L,
                        imageUrl = document.getString("imageUrl"),
                        creatorId = document.getString("creatorId"),
                        creatorName = document.getString("creatorName"),
                        isPrivate = document.getBoolean("isPrivate") ?: true,
                        tracks = trackIds
                    )
                }

                trySend(remotePlaylists)

            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun addTrackToPlayList(playListId: String, trackId: String) {

        val currentUserId = firebaseAuth.currentUser?.uid
            ?: throw Exception("Пользователь не авторизирован")

        try {
            firestore.collection("playlists")
                .document(playListId)
                .update("tracks", FieldValue.arrayUnion(trackId))
                .await()

            val cachedPlaylists = playListDao.getPlaylistsOnce(
                currentUserId
            )

            val currentPlaylist = cachedPlaylists.find { it.id == playListId }

            currentPlaylist?.let { entity ->
                val updatedTracks = entity.tracks.toMutableList().apply {
                    if (!contains(trackId)) add(trackId)
                }

                playListDao.insertPlaylists(listOf(entity.copy(tracks = updatedTracks)))
            }

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeTrackFromPlayList(playListId: String, trackId: String) {
        try {
            firestore.collection("playlists")
                .document(playListId)
                .update("tracks", FieldValue.arrayRemove(trackId))
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

}