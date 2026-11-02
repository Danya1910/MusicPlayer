package com.example.musicplayer.data.repositoryImpl

import android.content.Context
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,

    ) : FileRepository {

    override suspend fun downloadTrackToFile(
        trackId: String,
        networkUrl: String
    ): Flow<DownloadProgress> =
        flow {
            val file = File(context.filesDir, "tracks/$trackId.mp3")
            file.parentFile?.mkdirs()

            val connection = (URL(networkUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 15_000
                requestMethod = "GET"
                connect()
            }

            try {
                if (connection.responseCode !in 200..299) {
                    emit(
                        DownloadProgress.Failed(
                            IOException(
                                "HTTP ${connection.responseCode}"
                            )
                        )
                    )
                    return@flow
                }

                val totalBytes = connection.contentLengthLong
                var downloaded = 0L

                connection.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var read: Int
                        var lastEmit = 0L

                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloaded += read

                            val now = System.currentTimeMillis()
                            if (now - lastEmit > 100) {
                                emit(
                                    DownloadProgress.InProgress(
                                        downloaded, totalBytes
                                    )
                                )
                                lastEmit = now
                            }
                        }
                        output.flush()
                    }
                }

                emit(DownloadProgress.InProgress(downloaded, totalBytes))
                trackDao.updateAudioPath(trackId, file.absolutePath)
                emit(DownloadProgress.Completed(file))

            } catch (e: CancellationException) {
                file.delete()
                throw e
            } catch (e: Exception) {
                file.delete()
                emit(DownloadProgress.Failed(e))
            } finally {
                connection.disconnect()
            }
        }.flowOn(Dispatchers.IO)



    override suspend fun deleteTrackFromFile(trackId: String) {
        withContext(Dispatchers.IO) {
            try {

                val file = File(context.filesDir, "tracks/$trackId.mp3")
                if (file.exists()) {
                    file.delete()
                }

                trackDao.updateAudioPath(trackId, null)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}