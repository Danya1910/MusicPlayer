package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.DownloadProgress
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FileRepository {

    suspend fun downloadTrackToFile(
        trackId: String,
        networkUrl: String,
    ) : Flow<DownloadProgress>

    suspend fun deleteTrackFromFile(
        trackId: String,
    )

}