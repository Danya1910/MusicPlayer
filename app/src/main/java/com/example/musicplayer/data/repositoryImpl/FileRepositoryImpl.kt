package com.example.musicplayer.data.repositoryImpl

import android.content.Context
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,
) : FileRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _downloads = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    override val downloads: StateFlow<Map<String, DownloadProgress>> = _downloads.asStateFlow()

    private val jobs = ConcurrentHashMap<String, Job>()

    override fun toggleDownload(track: Track) {
        val id = track.id

        if (_downloads.value[id] is DownloadProgress.InProgress) {
            cancelDownload(id)
            return
        }

        if (track.localAudioPath != null) {
            scope.launch { deleteFile(id) }
            return
        }

        startDownload(id, track.audioUrl)
    }

    override fun cancelDownload(trackId: String) {
        jobs.remove(trackId)?.cancel()
        _downloads.update { it - trackId }
    }

    private fun startDownload(trackId: String, url: String) {
        jobs[trackId] = scope.launch {
            _downloads.update { it + (trackId to DownloadProgress.InProgress(0L, 0L)) }

            try {
                downloadInternal(trackId, url)
                val file = File(context.filesDir, "tracks/$trackId.mp3")
                _downloads.update { it + (trackId to DownloadProgress.Completed(file)) }
            } catch (e: CancellationException) {
                File(context.filesDir, "tracks/$trackId.mp3").delete()
                throw e
            } catch (e: Exception) {
                File(context.filesDir, "tracks/$trackId.mp3").delete()
                _downloads.update { it + (trackId to DownloadProgress.Failed(e)) }
            } finally {
                jobs.remove(trackId)
            }

            delay(1_500)
            _downloads.update { it - trackId }
        }
    }

    private suspend fun downloadInternal(trackId: String, networkUrl: String) = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "tracks/$trackId.mp3")
        file.parentFile?.mkdirs()

        val connection = (URL(networkUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 15_000
            connect()
        }

        try {
            if (connection.responseCode !in 200..299) {
                error("HTTP ${connection.responseCode}")
            }

            val total = connection.contentLengthLong
            var downloaded = 0L
            var lastEmit = 0L

            connection.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloaded += read

                        val now = System.currentTimeMillis()
                        if (now - lastEmit > 100) {
                            _downloads.update {
                                it + (trackId to DownloadProgress.InProgress(downloaded, total))
                            }
                            lastEmit = now
                        }
                    }
                }
            }

            trackDao.updateAudioPath(trackId, file.absolutePath)
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun deleteFile(trackId: String) = withContext(Dispatchers.IO) {
        if (jobs[trackId]?.isActive == true) return@withContext
        File(context.filesDir, "tracks/$trackId.mp3").delete()
        trackDao.updateAudioPath(trackId, null)
    }
}