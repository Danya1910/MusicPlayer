package com.example.musicplayer.domain.repository

import java.io.File

interface FileRepository {

    suspend fun downloadTrackToFile(
        trackId: String,
        networkUrl: String,
    ) : File?

    suspend fun deleteTrackFromFile(
        trackId: String,
    )

}