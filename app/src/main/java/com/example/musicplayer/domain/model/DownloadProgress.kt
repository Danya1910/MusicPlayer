package com.example.musicplayer.domain.model

import java.io.File

sealed interface DownloadProgress {

    data class InProgress(
        val bytesDownloaded: Long,
        val totalBytes: Long
    ) : DownloadProgress {
        val percent: Int
            get() = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt()
            else -1
    }
    data class Completed(val file: File) : DownloadProgress
    data class Failed(val error: Throwable?) : DownloadProgress

}