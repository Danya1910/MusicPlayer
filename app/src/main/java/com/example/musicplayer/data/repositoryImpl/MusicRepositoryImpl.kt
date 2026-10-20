package com.example.musicplayer.data.repositoryImpl

import android.content.ComponentName
import android.content.Context
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
import javax.inject.Inject


class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val trackDao: TrackDao,
) : MusicRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var mediaController: MediaController? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack = _currentTrack.asStateFlow()


    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    override val duration: StateFlow<Long> get() = MutableStateFlow(
        mediaController?.duration?.coerceAtLeast(0L) ?: 0L
    )

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

    override fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    override fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }


}