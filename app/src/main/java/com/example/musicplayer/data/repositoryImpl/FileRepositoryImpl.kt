package com.example.musicplayer.data.repositoryImpl

import android.content.Context
import com.example.musicplayer.data.local.dao.TrackDao
import com.example.musicplayer.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackDao: TrackDao,

): FileRepository {

    override suspend fun downloadTrackToFile(trackId: String, networkUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "tracks/$trackId.mp3")
                file.parentFile?.mkdirs()

                val url = URL(networkUrl)
                url.openStream().use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                trackDao.updateAudioPath(trackId, file.absolutePath)

                file

            } catch (e: Exception) {
                e.printStackTrace()
                val file = File(context.filesDir, "tracks/$trackId.mp3")
                if (file.exists()) file.delete()
                null
            }
        }
    }

    override suspend fun deleteTrackFromFile(trackId: String) {
        withContext(Dispatchers.IO) {
            try {

                val file = File(context.filesDir, "tracks/$trackId.mp3")
                if(file.exists()) {
                    file.delete()
                }

                trackDao.updateAudioPath(trackId, null)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}