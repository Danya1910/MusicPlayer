package com.example.musicplayer.presentation.main

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.model.Lyrics
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.model.User
import com.example.musicplayer.domain.repository.FileRepository
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.domain.repository.TrackRepository
import com.example.musicplayer.domain.repository.UserRepository
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.presentation.main.util.PlaylistUi
import com.example.musicplayer.presentation.main.util.ToastData
import com.example.musicplayer.presentation.main.util.ToastType
import com.example.musicplayer.presentation.main.util.getTracksTotalTime
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import com.google.protobuf.LazyStringArrayList.emptyList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@Suppress("UNCHECKED_CAST")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val musicRepository: MusicRepository,
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val downloads: StateFlow<Map<String, DownloadProgress>> = fileRepository.downloads

    val isSleepTimerActive: StateFlow<Boolean> =
        combine(
            musicRepository.sleepTimerRemainingSec,
            musicRepository.stopAtTrackEnd
        ) { remaining, atEnd ->
            remaining != null || atEnd
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _toast = MutableStateFlow<ToastData?>(null)
    val toast = _toast.asStateFlow()

    private val _lyrics = MutableStateFlow<Lyrics?>(null)
    val lyrics = _lyrics.asStateFlow()

    private var lyricsJob: Job? = null

    companion object {
        private const val FAVORITES_ID = "favorites"

        val EmptyFavorites = PlaylistUi(
            playlist = PlayList(
                id = FAVORITES_ID,
                name = "Liked songs",
                updatedAt = 0L,
                imageUrl = "",
                creatorId = "",
                creatorName = "",
                isPrivate = true,
                tracks = kotlin.collections.emptyList(),
                duration = ""
            ),
            tracks = kotlin.collections.emptyList()
        )
    }

    val uiState: StateFlow<MainUiState> = combine(
        trackRepository.getAllTrack(),
        trackRepository.getAlbums(),
    ) { tracks, albums ->
        Log.d(
            "TracksDebug",
            "Tracks emitted: ${tracks.count { it.isFavorite }}"
        )
        MainUiState.Success(tracks = tracks, albums = albums) as MainUiState
    }
        .catch { exception ->
            emit(MainUiState.Error(exception.localizedMessage ?: "Неизвестная ошибка"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )

    val playlistsState: StateFlow<List<PlayList>> = trackRepository.getUserPlayLists()
        .catch { exception ->
            exception.printStackTrace()
            emit(emptyList<PlayList>())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ) as StateFlow<List<PlayList>>


    val playlistsUiState: StateFlow<List<PlaylistUi>> = combine(
        playlistsState,
        uiState
    ) { playlists, mainUiState ->

        if (mainUiState !is MainUiState.Success) {
            return@combine playlists.map {
                PlaylistUi(
                    playlist = it,
                    tracks = emptyList<Track>()
                )
            }
        }

        val allTracks = mainUiState.tracks

        playlists.map { playlist ->
            val orderedTracks = playlist.tracks?.mapNotNull { trackId ->
                allTracks.find { it.id == trackId }
            } ?: emptyList()

            PlaylistUi(
                playlist = playlist,
                tracks = orderedTracks as List<Track>
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ) as StateFlow<List<PlaylistUi>>

    val favoritesPlaylist: StateFlow<PlaylistUi> = uiState
        .map { mainUiState ->
            if (mainUiState !is MainUiState.Success) {
                return@map EmptyFavorites
            }

            val favoriteTracks = mainUiState.tracks.filter { it.isFavorite }

            Log.d("FavoritesDebug", "Favorites count: ${favoriteTracks.size}")


            val favoritesDuration = getTracksTotalTime(
                tracks = favoriteTracks,
            )

            PlaylistUi(
                playlist = PlayList(
                    id = FAVORITES_ID,
                    name = "Liked songs",
                    updatedAt = 0L,
                    imageUrl = "",
                    creatorId = "",
                    creatorName = "",
                    isPrivate = true,
                    tracks = favoriteTracks.map { it.id },
                    duration = favoritesDuration,
                ),
                tracks = favoriteTracks,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmptyFavorites
        )

    val allPlaylistsWithFavorites: StateFlow<List<PlayList>> = combine(
        favoritesPlaylist,
        playlistsUiState
    ) { favorites, playlists ->
        listOf(favorites.playlist) + playlists.map { it.playlist }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(EmptyFavorites.playlist)
    )

    val userState: StateFlow<User> = userRepository.getUserData()
        .catch { exception ->
            exception.printStackTrace()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        ) as StateFlow<User>

    val currentTrack = musicRepository.currentTrack
    val isPlaying = musicRepository.isPlaying
    private val _paletteState =
        MutableStateFlow<Map<String, Triple<Color, Color, Color>>>(emptyMap())

    val playerState: StateFlow<PlayerUiState> = combine(
        musicRepository.currentTrack,
        musicRepository.isPlaying,
        musicRepository.currentPosition,
        musicRepository.duration,
        musicRepository.isShuffleModeEnabled,
        musicRepository.repeatMode,
        _paletteState
    ) { flowsArray ->
        val track = flowsArray[0] as Track?
        val isPlaying = flowsArray[1] as Boolean
        val position = flowsArray[2] as Long
        val duration = flowsArray[3] as Long
        val isShuffle = flowsArray[4] as Boolean
        val repeatMode = flowsArray[5] as Int
        val paletteMap = flowsArray[6] as Map<String, Triple<Color, Color, Color>>

        val colors = if (track != null) {
            paletteMap[track.id] ?: Triple(DeepBlack, White, Gray)
        } else {
            Triple(DeepBlack, White, Gray)
        }

        PlayerUiState(
            currentTrack = track,
            isPlaying = isPlaying,
            currentPosition = position,
            duration = duration,
            bgColor = colors.first,
            titleColor = colors.second,
            bodyColor = colors.third,
            paletteState = paletteMap,
            isShuffleEnabled = isShuffle,
            repeatMode = repeatMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState()
    )

    val currentQueue: StateFlow<List<Track>> = musicRepository.currentQueue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ) as StateFlow<List<Track>>

    val upcomingTracks: StateFlow<List<Track>> = combine(
        musicRepository.currentQueue,
        musicRepository.currentTrack
    ) { queue: List<Track>, currentTrack: Track? ->
        if (currentTrack == null) return@combine emptyList()

        val currentIndex = queue.indexOfFirst { it.id == currentTrack.id }
        if (currentIndex == -1 || currentIndex == queue.lastIndex) {
            emptyList()
        } else {
            queue.subList(currentIndex + 1, queue.size).toList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ) as StateFlow<List<Track>>

    fun playTrack(track: Track) {
        musicRepository.playTrack(track)
    }

    fun playTrackWithQueue(
        track: Track,
        queue: List<Track>
    ) {
        musicRepository.playTrackWithQueue(
            track = track,
            queue = queue,
        )
    }

    fun togglePlayPause() {
        musicRepository.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        musicRepository.seekTo(positionMs = positionMs)
    }

    fun moveTrack(fromIndex: Int, toIndex: Int) {
        musicRepository.moveTrackInQueue(fromIndex, toIndex)
    }

    fun removeTrackFromQueue(index: Int) {
        musicRepository.removeTrackFromQueue(
            index = index,
        )
    }


    fun updateColorsFromBitmap(id: String, bitmap: Bitmap) {
        if (_paletteState.value.containsKey(id)) return

        viewModelScope.launch(Dispatchers.Default) {
            val configBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }

            val finalBitmap = configBitmap ?: bitmap

            val palette = Palette.from(finalBitmap).generate()

            val swatch = palette.darkVibrantSwatch
                ?: palette.darkMutedSwatch
                ?: palette.dominantSwatch

            if (swatch != null) {
                val newColors = Triple(
                    Color(swatch.rgb),
                    Color(swatch.titleTextColor),
                    Color(swatch.bodyTextColor)
                )

                withContext(Dispatchers.Main) {
                    _paletteState.value = _paletteState.value + (id to newColors)
                }
            }
        }
    }


    fun skipToNext() {
        musicRepository.skipToNext()
    }

    fun skipToPrevious() {
        musicRepository.skipToPrevious()
    }

    fun toggleShuffleMode() {
        musicRepository.toggleShuffleMode()
    }

    fun playNext(
        track: Track,
    ) {
        musicRepository.addTrackToPlayNext(
            track = track,
        )
    }

    fun toggleRepeatMode() {
        musicRepository.toggleRepeatMode()
    }

    fun createPlayList(
        title: String,
        imageUrl: String,
    ) {
        viewModelScope.launch {
            try {

                trackRepository.createPlayList(
                    title = title,
                    imageUrl = imageUrl,
                )
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun createPlayListWithTrack(
        title: String,
        trackId: String,
        imageUrl: String,
    ) {
        viewModelScope.launch {
            try {
                val newPlayListId = trackRepository.createPlayList(
                    title = title,
                    imageUrl = imageUrl,
                )

                trackRepository.addTrackToPlayList(
                    playListId = newPlayListId,
                    trackId = trackId,
                )
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun addTrackToPlayList(
        playListId: String,
        trackId: String,
    ) {
        if (playListId == FAVORITES_ID) {
            likeTrack(trackId = trackId)
            return
        }

        viewModelScope.launch {
            try {
                trackRepository.addTrackToPlayList(
                    playListId = playListId,
                    trackId = trackId,
                )
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun removeTrackFromPlayList(
        playListId: String,
        trackId: String,
    ) {
        if (playListId == FAVORITES_ID) {
            unlikeTrack(trackId = trackId)
            return
        }
        viewModelScope.launch {
            try {

                trackRepository.removeTrackFromPlayList(
                    playListId = playListId,
                    trackId = trackId,
                )
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun likeTrack(
        trackId: String,
    ) {
        musicRepository.updateFavoriteStatus(trackId, isFavorite = true)

        viewModelScope.launch {
            try {
                trackRepository.likeTrack(
                    trackId = trackId,
                )
                _toast.value = ToastData(
                    title = "Favorites",
                    message = "Added to Liked Songs",
                    type = ToastType.SUCCESS,
                )
                Log.d("Like", "Liked success")
            } catch (e: Exception) {
                Log.e("TrackVM", e.toString())
                musicRepository.updateFavoriteStatus(trackId, isFavorite = false)
                _toast.value = ToastData(
                    title = "Favorites",
                    message = "Fail Adding to Liked Songs",
                    type = ToastType.ERROR,
                )
                Log.d("Like", "Liked exception")

            }
        }
    }

    fun unlikeTrack(
        trackId: String,
    ) {
        musicRepository.updateFavoriteStatus(trackId, isFavorite = false)

        viewModelScope.launch {
            try {
                trackRepository.unlikeTrack(
                    trackId = trackId,
                )
            } catch (e: Exception) {
                Log.e("TrackVM", e.toString())
                musicRepository.updateFavoriteStatus(trackId, isFavorite = true)
            }
        }
    }

    fun toggleFavorite(track: Track) {
        if (track.isFavorite) unlikeTrack(track.id)
        else likeTrack(track.id)
    }

    fun likeAlbum(
        albumId: String
    ) {

        viewModelScope.launch {
            try {
                trackRepository.likeAlbum(
                    albumId = albumId,
                )
                _toast.value = ToastData(
                    title = "Library",
                    message = "Added to your Library",
                    type = ToastType.SUCCESS,
                )
                Log.d("Like", "Liked success")
            } catch (e: Exception) {
                Log.e("TrackVM", e.toString())
                _toast.value = ToastData(
                    title = "Library",
                    message = "Fail Adding to your Library",
                    type = ToastType.ERROR,
                )
                Log.d("Like", "Liked exception")

            }
        }
    }

    fun unlikeAlbum(
        albumId: String,
    ) {
        viewModelScope.launch {
            try {
                trackRepository.unlikeAlbum(
                    albumId = albumId,
                )
            } catch (e: Exception) {
                Log.e("TrackVM", e.toString())
            }
        }
    }

    fun toggleAlbumLike(
        album: Album,
    ) {
        if (album.isFavorite) unlikeAlbum(albumId = album.id)
        else likeAlbum(albumId = album.id)
    }

    fun onDownloadClick(track: Track) = fileRepository.toggleDownload(track)

    fun toggleListOfTracks(
        tracks: List<Track>
    ) {
        try {
            fileRepository.toggleDownloadList(
                tracks = tracks
            )
        } catch (e: Exception) {
            Log.d("TrackVM", e.toString())
        }
    }

    fun getLyrics(
        trackId: String,
    ) {
        lyricsJob?.cancel()

        _lyrics.value = null

        lyricsJob = viewModelScope.launch {
            try {
                _lyrics.value = trackRepository.getLyrics(
                    trackId = trackId
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Lyrics", "Failed to load lyrics", e)
            }
        }
    }

    fun setSleepTime(
        minutes: Int,
    ) {
        viewModelScope.launch {
            try {
                musicRepository.setSleepTimer(
                    minutes = minutes,
                )
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun stopOnTrackEnd() {
        viewModelScope.launch {
            try {
                musicRepository.setSleepTimerToTrackEnd()
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun cancelSleepTimer() {
        viewModelScope.launch {
            try {
                musicRepository.cancelSleepTimer()
            } catch (e: Exception) {
                Log.d("TrackVM", e.toString())
            }
        }
    }

    fun resetToast() {
        _toast.value = null
    }

}