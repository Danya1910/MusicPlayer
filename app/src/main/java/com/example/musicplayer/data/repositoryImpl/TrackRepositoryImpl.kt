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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
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

        val userName = getCurrentUserName()

        val playList = PlayList(
            name = title,
            updatedAt = System.currentTimeMillis(),
            creatorId = currentUser?.uid,
            creatorName = userName,
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

    private fun getRawUserPlayLists(): Flow<List<PlayList>> = callbackFlow {
        val currentUserId = firebaseAuth.currentUser?.uid
            ?: throw Exception("Пользователь не авторизирован")

        // Отправляем локальные данные из Room для мгновенного отображения
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
                        tracks = trackIds,
                        duration = "00:00" // Временно ставим дефолт
                    )
                }
                trySend(remotePlaylists)
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }.flowOn(Dispatchers.IO)

    // 3. ПУБЛИЧНЫЙ МЕТОД: Соединяет плейлисты с треками и вычисляет длительность
    override fun getUserPlayLists(): Flow<List<PlayList>> {
        // Получаем ваши треки (подставьте сюда ваш реальный метод получения треков, например из Room)
        val allTracksFlow: Flow<List<Track>> = trackDao.getAllTracksFlow().map { list -> list.map { it.toDomain() } }

        return getRawUserPlayLists().combine(allTracksFlow) { playlists, allTracks ->
            playlists.map { playlist ->
                // Фильтруем все доступные треки, находя те, чьи ID есть в данном плейлисте
                val playlistTracks = allTracks.filter { track ->
                    playlist.tracks?.contains(track.id) == true
                }

                // Суммируем длительность (предполагаем, что track.duration хранится в мс)
                val totalMs = playlistTracks.sumOf { it.duration }

                // Возвращаем обновленный плейлист с посчитанным временем
                playlist.copy(
                    duration = formatDuration(totalMs)
                )
            }
        }.flowOn(Dispatchers.Default) // Расчеты лучше делать на Default-диспетчере
    }


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

    private suspend fun getCurrentUserName(): String? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            Log.e("TrackRepo", "getCurrentUserName: currentUser.uid IS NULL! Пользователь не авторизован в Auth.")
            return null
        }

        Log.d("TrackRepo", "getCurrentUserName: Начинаем запрос в Firestore для UID: $uid")

        return try {
            val document = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (document.exists()) {
                val name = document.getString("name")
                Log.d("TrackRepo", "getCurrentUserName: Документ найден! Имя в БД: '$name'")
                name
            } else {
                Log.e("TrackRepo", "getCurrentUserName: Документ с UID $uid НЕ НАЙДЕН в коллекции 'users'!")
                null
            }
        } catch (e: Exception) {
            // Если тут выпадет Permission Denied — нужно поправить Rules в консоли Firebase
            Log.e("TrackRepo", "getCurrentUserName: Исключение при запросе к Firestore для UID: $uid", e)
            null
        }
    }


}


private fun formatDuration(durationMs: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

    return if (hours > 0) {
        String.format("%d ч %02d мин", hours, minutes)
    } else {
        String.format("%02d", minutes)
    }
}