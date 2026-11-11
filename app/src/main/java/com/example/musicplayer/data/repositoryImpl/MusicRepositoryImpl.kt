package com.example.musicplayer.data.repositoryImpl

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val trackDao: TrackDao,
) : MusicRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _sleepTimerRemainingSec = MutableStateFlow<Long?>(null)

    override val sleepTimerRemainingSec = _sleepTimerRemainingSec.asStateFlow()
    private var sleepTimerJob: Job? = null

    private val _trackCache = MutableStateFlow<Map<String, Track>>(emptyMap())

    private val _stopAtTrackEnd = MutableStateFlow(false)

    override val stopAtTrackEnd = _stopAtTrackEnd.asStateFlow()

    private var mediaController: MediaController? = null

    private val _currentTrackId = MutableStateFlow<String?>(null)
    override val currentTrackId = _currentTrackId.asStateFlow()

    override val currentTrack: StateFlow<Track?> = _currentTrackId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else trackDao.observeTrackById(id).map { it?.toDomain() }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    override val isShuffleModeEnabled = _isShuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(0)
    override val repeatMode = _repeatMode.asStateFlow()


    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

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
                syncQueueFromController()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentTrackId.value = mediaItem?.mediaId
                _duration.value = 0L

                syncQueueFromController()

                if (_stopAtTrackEnd.value && reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    _stopAtTrackEnd.value = false
                    mediaController?.pause()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                syncQueueFromController()
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
        _trackCache.value = _trackCache.value + (track.id to track)

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setArtworkUri(Uri.parse(track.imageUrl))
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(track.playbackUri())
            .setMediaMetadata(mediaMetadata)
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }


    override fun playTrackWithQueue(track: Track, queue: List<Track>) {
        _trackCache.value = _trackCache.value + queue.associateBy { it.id }

        repositoryScope.launch(Dispatchers.Default) {
            val mediaItems = queue.map { qTrack ->
                val metadata = MediaMetadata.Builder()
                    .setTitle(qTrack.title)
                    .setArtist(qTrack.artist)
                    .setArtworkUri(Uri.parse(qTrack.imageUrl))
                    .build()

                MediaItem.Builder()
                    .setMediaId(qTrack.id)
                    .setUri(qTrack.playbackUri())
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
            syncQueueFromController()
        }
    }

    override fun toggleRepeatMode() {
        mediaController?.let { player ->
            val nextRepeatMode = when (player.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            player.repeatMode = nextRepeatMode
        }
    }

    override fun addTrackToPlayNext(track: Track) {
        _trackCache.value = _trackCache.value + (track.id to track)
        val controller = mediaController ?: return

        val currentIndex = mediaController?.currentMediaItemIndex ?: 0

        val metaData = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setArtworkUri(Uri.parse(track.imageUrl))
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(track.playbackUri())
            .setMediaMetadata(metaData)
            .build()

        val targetIndex = if (currentIndex != -1) currentIndex + 1 else controller.mediaItemCount

        controller.addMediaItem(targetIndex, mediaItem)

    }

    override fun moveTrackInQueue(fromIndex: Int, toIndex: Int) {
        val controller = mediaController ?: return
        if (controller.shuffleModeEnabled) return   // при shuffle индексы UI не совпадают с timeline
        val totalItems = controller.mediaItemCount
        if (fromIndex in 0 until totalItems && toIndex in 0 until totalItems) {
            controller.moveMediaItem(fromIndex, toIndex)
        }
    }

    override fun removeTrackFromQueue(index: Int) {
        val controller = mediaController ?: return
        if (controller.shuffleModeEnabled) return   // или маппинг индексом
        val totalItemsInPlayer = controller.mediaItemCount
        if (index in 0 until totalItemsInPlayer) {
            controller.removeMediaItem(index)
        } else {
            Log.e("MusicPlayer_Repo", "Индекс $index вне границ (0..${totalItemsInPlayer - 1})")
        }
    }

    override fun updateFavoriteStatus(trackId: String, isFavorite: Boolean) {
        repositoryScope.launch(Dispatchers.IO) {
            trackDao.updateFavoriteStatus(trackId, isFavorite)
        }
    }

    override fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        _stopAtTrackEnd.value = false
        val totalMs = minutes * 60_000L

        _sleepTimerRemainingSec.value = totalMs / 1000

        sleepTimerJob = repositoryScope.launch {
            val start = System.currentTimeMillis()
            while (true) {
                val elapsed = System.currentTimeMillis() - start
                val remaining = totalMs - elapsed
                if(remaining <= 0L) {
                    _sleepTimerRemainingSec.value = null
                    withContext(Dispatchers.Main) {
                        mediaController?.pause()
                    }
                    break
                }
                _sleepTimerRemainingSec.value = remaining / 1000
                delay(1000)
            }
        }

    }

    override fun setSleepTimerToTrackEnd() {
        cancelSleepTimer()
        _stopAtTrackEnd.value = true
        _sleepTimerRemainingSec.value = null
    }

    override fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _stopAtTrackEnd.value = false
        _sleepTimerRemainingSec.value = null
    }

    private fun syncQueueFromController() {
        val controller = mediaController ?: return
        val idToTrack = _trackCache.value
        val timeline = controller.currentTimeline
        val shuffle = controller.shuffleModeEnabled

        if (timeline.isEmpty) {
            _currentQueue.value = emptyList()
            _currentTrackId.value = controller.currentMediaItem?.mediaId
            return
        }

        val order = mutableListOf<Track>()
        var windowIndex = timeline.getFirstWindowIndex(shuffle)
        while (windowIndex != C.INDEX_UNSET) {
            val mediaId = timeline.getWindow(windowIndex, Timeline.Window()).mediaItem.mediaId
            idToTrack[mediaId]?.let(order::add)
            windowIndex = timeline.getNextWindowIndex(
                windowIndex,
                Player.REPEAT_MODE_OFF,
                shuffle
            )
        }

        _currentQueue.value = order
        _currentTrackId.value = controller.currentMediaItem?.mediaId
    }

    private fun Track.playbackUri(): Uri =
        localAudioPath
            ?.takeIf { it.isNotBlank() }
            ?.let { Uri.fromFile(File(it)) }
            ?: Uri.parse(audioUrl)

}