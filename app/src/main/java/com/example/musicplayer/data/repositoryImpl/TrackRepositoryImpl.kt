package com.example.musicplayer.data.repositoryImpl

import android.util.Log
import com.example.musicplayer.data.local.dao.AlbumDao
import com.example.musicplayer.data.local.dao.LyricDao
import com.example.musicplayer.data.local.dao.PlayListDao
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.AlbumEntity
import com.example.musicplayer.data.local.entity.FavoriteTrackEntity
import com.example.musicplayer.data.local.entity.TrackEntity
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.local.entity.toEntity
import com.example.musicplayer.data.wrapper.toDomain
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Lyrics
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.TrackRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TrackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val playListDao: PlayListDao,
    private val lyricDao: LyricDao,
) : TrackRepository {

    private var playlistsListener: ListenerRegistration? = null
    private var userPlaylistsRegistration: ListenerRegistration? = null

    override fun getAllTrack(): Flow<List<Track>> = flow {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            emit(emptyList())
            return@flow
        }

        try {
            coroutineScope {
                val favoriteDeferred = async {
                    firestore.collection("favorites").document(userId)
                        .collection("tracks").get().await().map { it.id }
                }
                val tracksDeferred = async {
                    firestore.collection("tracks").get().await()
                }
                val favoriteIds = favoriteDeferred.await()
                val allTracksSnapshot = tracksDeferred.await()

                val cacheMap = trackDao.getAllTracksOnce().associateBy { it.id }
                val entities = allTracksSnapshot.documents.map { doc ->
                    val cached = cacheMap[doc.id]
                    TrackEntity(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        audioUrl = doc.getString("audioUrl") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        duration = doc.getLong("duration") ?: 0L,
                        genre = doc.getString("genre") ?: "",
                        albumName = doc.getString("albumName") ?: "",
                        albumId = doc.getString("albumId") ?: "",
                        localAudioPath = cached?.localAudioPath,   // ✅ не теряем
                    )
                }
                trackDao.insertTracks(entities)

                trackDao.clearFavoritesForUser(userId)
                val now = System.currentTimeMillis()
                trackDao.addFavorites(favoriteIds.map { FavoriteTrackEntity(userId, it, now) })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // не пробрасываем — ниже всё равно отдадим то, что есть в Room
        }

        emitAll(
            combine(
                trackDao.getAllTracksFlow(),
                trackDao.observeFavoriteIds(userId)
            ) { tracks, favoriteIds ->
                val favSet = favoriteIds.toSet()
                tracks.map { entity ->
                    entity.toDomain().copy(isFavorite = entity.id in favSet)
                }
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun getLyrics(trackId: String): Lyrics? =
        withContext(Dispatchers.IO) {
            val cachedLyrics = lyricDao.getLyricsOnce(trackId)

            if (cachedLyrics != null) {
                return@withContext cachedLyrics.toDomain()
            }

            val remoteLyrics = firestore
                .collection("lyrics")
                .document(trackId)
                .get()
                .await()

            if (!remoteLyrics.exists()) return@withContext null

            val lyrics = remoteLyrics.toObject<Lyrics>() ?: return@withContext null

            lyricDao.insertLyrics(lyrics.toEntity())

            lyrics
        }

    override fun getAlbums(): Flow<List<Album>> = flow {

        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null) {
            emit(emptyList())
            return@flow
        }

        val savedAlbums = albumDao.getAllAlbumsOnce().map { it.toDomain() }
        emit(savedAlbums)

        try {
            coroutineScope {
                val favoriteDeferred = async {
                    firestore.collection("favorites")
                        .document(currentUserId)
                        .collection("albums")
                        .get()
                        .await()
                        .map { it.id }
                        .toSet()
                }
                val albumsDeferred = async {
                    firestore.collection("albums").get().await()
                }

                val favoriteAlbumIds = favoriteDeferred.await()
                val allAlbumsSnapshot = albumsDeferred.await()

                val currentCache = albumDao.getAllAlbumsOnce()
                val cacheMap = currentCache.associateBy { it.albumId }

                val remoteAlbumsEntities = allAlbumsSnapshot.documents.map { document ->
                    val albumId = document.id
                    val cached = cacheMap[albumId]

                    val isFavorite = favoriteAlbumIds.contains(albumId)

                    Log.d(
                        "AlbumDebug",
                        "merge album=$albumId cached=${cached?.isFavorite} " +
                                "remote=${favoriteAlbumIds.contains(albumId)} -> $isFavorite"
                    )

                    AlbumEntity(
                        albumId = albumId,
                        title = document.getString("title") ?: "Без названия",
                        artist = document.getString("artist") ?: "Неизвестный исполнитель",
                        imageUrl = document.getString("imageUrl") ?: "",
                        isFavorite = isFavorite,
                    )
                }

                albumDao.insertAlbums(remoteAlbumsEntities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        emitAll(
            albumDao.getAllAlbumsWithTracks().map { albums ->
                albums.map { it.toDomain() }
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun createPlayList(
        title: String,
        imageUrl: String,
    ): String {

        val currentUser = firebaseAuth.currentUser

        val userName = getCurrentUserName()

        val playList = PlayList(
            name = title,
            updatedAt = System.currentTimeMillis(),
            creatorId = currentUser?.uid,
            creatorName = userName,
            imageUrl = imageUrl,
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
        if (currentUserId == null) {
            send(emptyList())
            close()
            return@callbackFlow
        }

        val savedPlaylists = playListDao.getPlaylistsOnce(currentUserId).map { it.toDomain() }
        send(savedPlaylists)

        val query = firestore.collection("playlists")
            .whereEqualTo("creatorId", currentUserId)

        userPlaylistsRegistration = query.addSnapshotListener { snapshot, error ->
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
                        duration = "0 min"
                    )
                }
                trySend(remotePlaylists)
            }
        }

        awaitClose {
            userPlaylistsRegistration?.remove()
            userPlaylistsRegistration = null
        }

    }.flowOn(Dispatchers.IO)

    override fun getUserPlayLists(): Flow<List<PlayList>> {
        val allTracksFlow: Flow<List<Track>> = trackDao.getAllTracksFlow().map { list ->
            list.map {
                it.toDomain()
            }
        }

        return getRawUserPlayLists().combine(allTracksFlow) { playlists, allTracks ->
            if (firebaseAuth.currentUser?.uid == null) return@combine emptyList()

            playlists.map { playlist ->
                val playlistTracks = allTracks.filter { track ->
                    playlist.tracks?.contains(track.id) == true
                }

                val totalMs = playlistTracks.sumOf { it.duration }

                playlist.copy(
                    duration = formatDuration(totalMs)
                )
            }
        }.flowOn(Dispatchers.Default)
    }


    override suspend fun addTrackToPlayList(playListId: String, trackId: String) {

        val currentUserId = firebaseAuth.currentUser?.uid ?: return

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
        val uid = firebaseAuth.currentUser?.uid ?: return null

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
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun likeTrack(trackId: String) {

        val uid = firebaseAuth.currentUser?.uid ?: return
        val now = System.currentTimeMillis()

        trackDao.addFavorite(FavoriteTrackEntity(uid, trackId, now))


        try {
            val likeData = mapOf(
                "likedAt" to System.currentTimeMillis()
            )
            Log.d("TrackRepo", "start liking track with id:$trackId")

            firestore.collection("favorites")
                .document(uid)
                .collection("tracks")
                .document(trackId)
                .set(mapOf("likedAt" to now))
                .await()

            Log.d("TrackRepo", "track with id:$trackId liked")
        } catch (e: Exception) {
            trackDao.removeFavorite(uid, trackId)
            throw e
        }

    }

    override suspend fun likeAlbum(albumId: String) {
        val currentUseUid = firebaseAuth.currentUser?.uid ?: return

        albumDao.updateFavoriteStatus(albumId = albumId, isFavorite = true)

        try {
            val likedData = mapOf(
                "likedAt" to System.currentTimeMillis()
            )

            firestore.collection("favorites")
                .document(currentUseUid)
                .collection("albums")
                .document(albumId)
                .set(likedData)
                .await()

            Log.d("TrackRepo", "album with id:${albumId} liked")

        } catch (e: Exception) {
            e.printStackTrace()
            albumDao.updateFavoriteStatus(albumId = albumId, isFavorite = false)
            throw e
        }
    }


    override suspend fun unlikeTrack(trackId: String) {

        val uid = firebaseAuth.currentUser?.uid ?: return

        val wasFavorite = trackDao.getFavoriteIdsOnce(uid).contains(trackId)

        trackDao.removeFavorite(uid, trackId)

        try {
            Log.d("TrackRepo", "start deleting track with id:$trackId")

            firestore.collection("favorites")
                .document(uid)
                .collection("tracks")
                .document(trackId)
                .delete()
                .await()

            Log.d("TrackRepo", "track with id:$trackId deleted from favorites")
        } catch (e: Exception) {
            if (wasFavorite) {
                trackDao.addFavorite(
                    FavoriteTrackEntity(uid, trackId, System.currentTimeMillis())
                )
            }
            throw e
        }
    }

    override suspend fun unlikeAlbum(albumId: String) {
        val currentUserUid = firebaseAuth.currentUser?.uid ?: return

        albumDao.updateFavoriteStatus(albumId = albumId, isFavorite = false)

        try {

            firestore.collection("favorites")
                .document(currentUserUid)
                .collection("albums")
                .document(albumId)
                .delete()
                .await()

            Log.d("TrackRepo", "album with id:$albumId deleted from favorites")
        } catch (e: Exception) {
            e.printStackTrace()
            albumDao.updateFavoriteStatus(albumId = albumId, isFavorite = true)
            throw e
        }
    }

    override fun stopListening() {
        playlistsListener?.remove()
        playlistsListener = null

        userPlaylistsRegistration?.remove()
        userPlaylistsRegistration = null
    }

}


private fun formatDuration(durationMs: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

    return when {
        hours > 0 -> {
            if (minutes > 0) {
                String.format("%d h %d min", hours, minutes)
            } else {
                String.format("%d h", hours)
            }
        }

        minutes > 0 -> {
            String.format("%d min", minutes)
        }

        else -> {
            if (seconds > 0) {
                String.format("%d sec", seconds)
            } else {
                "0 min"
            }
        }
    }
}