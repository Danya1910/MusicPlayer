package com.example.musicplayer.data.repositoryImpl

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.data.local.entity.toDomain
import com.example.musicplayer.data.service.MusicService
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.MusicRepository
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val trackDao: TrackDao,
) : MusicRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var mediaController: MediaController? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack = _currentTrack.asStateFlow()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    override val  isShuffleModeEnabled = _isShuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(0)
    override val repeatMode = _repeatMode.asStateFlow()



    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    override val duration: StateFlow<Long>
        get() = MutableStateFlow(
            mediaController?.duration?.coerceAtLeast(0L) ?: 0L
        )

    private val _currentQueue = MutableStateFlow<List<Track>>(emptyList())
    override val currentQueue = _currentQueue.asStateFlow()

    init {
        val sessionToken = SessionToken(
            context, ComponentName(context, MusicService::class.java)
        )
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            setupPlayerListener()
            startProgressTracker()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _isShuffleModeEnabled.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val trackId = mediaItem?.mediaId

                if (trackId != null) {
                    repositoryScope.launch(Dispatchers.IO) {
                        val trackEntity = trackDao.getTrackById(trackId)

                        val fullTrack = trackEntity?.toDomain()

                        launch(Dispatchers.Main) {
                            _currentTrack.value = fullTrack
                        }
                    }
                } else {
                    _currentTrack.value = null
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _currentTrack.value = _currentTrack.value?.copy(
                        duration = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                    )
                }
            }
        })
    }


    private fun startProgressTracker() {
        repositoryScope.launch {
            while (true) {
                mediaController?.let {
                    if (it.isPlaying) _currentPosition.value = it.currentPosition
                }
                delay(1000)
            }
        }
    }

    override fun playTrack(track: Track) {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setArtworkUri(android.net.Uri.parse(track.imageUrl))
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(track.audioUrl)
            .setMediaMetadata(mediaMetadata)
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }


    override fun playTrackWithQueue(track: Track, queue: List<Track>) {
        _currentQueue.value = queue

        repositoryScope.launch(Dispatchers.Default) {
            val mediaItems = queue.map { qTrack ->
                val metadata = MediaMetadata.Builder()
                    .setTitle(qTrack.title)
                    .setArtist(qTrack.artist)
                    .setArtworkUri(Uri.parse(qTrack.imageUrl))
                    .build()

                MediaItem.Builder()
                    .setMediaId(qTrack.id)
                    .setUri(qTrack.audioUrl)
                    .setMediaMetadata(metadata)
                    .build()
            }

            withContext(Dispatchers.Main) {
                mediaController?.let { controller ->
                    controller.setMediaItems(mediaItems)

                    val startingIndex = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

                    controller.seekTo(startingIndex, 0L)
                    controller.prepare()
                    controller.play()
                }
            }

        }

    }


    override fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    override fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    override fun skipToNext() {
        mediaController?.let { controller ->
            if (controller.hasNextMediaItem()) {
                controller.seekToNextMediaItem()
                controller.play()
            }
        }
    }

    override fun skipToPrevious() {
        mediaController?.let { controller ->
            if (controller.hasPreviousMediaItem()) {
                controller.seekToPreviousMediaItem()
                controller.play()
            }
        }
    }

    override fun toggleShuffleMode() {
        mediaController?.let { player ->
            player.shuffleModeEnabled = !player.shuffleModeEnabled
        }
    }

    override fun toggleRepeatMode() {
        mediaController?.let { player ->
            val nextRepeatMode = when(player.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            player.repeatMode = nextRepeatMode
        }
    }

    override fun moveTrackInQueue(fromUpcomingIndex: Int, toUpcomingIndex: Int) {
        val controller = mediaController ?: return
        val currentIndex = controller.currentMediaItemIndex

        if (currentIndex == -1) return

        val realFromIndex = currentIndex + 1 + fromUpcomingIndex
        val realToIndex = currentIndex + 1 + toUpcomingIndex

        val totalItems = controller.mediaItemCount

        if (realFromIndex in 0 until totalItems && realToIndex in 0 until totalItems) {

            controller.moveMediaItem(realFromIndex, realToIndex)

            val updatedQueue = _currentQueue.value.toMutableList().apply {
                if (realFromIndex in indices && realToIndex in indices) {
                    add(realToIndex, removeAt(realFromIndex))
                }
            }
            _currentQueue.value = updatedQueue
        }
    }


}