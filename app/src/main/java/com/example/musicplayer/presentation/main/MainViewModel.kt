package com.example.musicplayer.presentation.main

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.model.User
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.domain.repository.TrackRepository
import com.example.musicplayer.domain.repository.UserRepository
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.presentation.main.util.PlaylistUi
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import com.google.protobuf.LazyStringArrayList.emptyList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository,
) : ViewModel() {


    val uiState: StateFlow<MainUiState> = combine(
        trackRepository.getAllTrack(),
        trackRepository.getAlbums(),
    ) { tracks, albums ->
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


}