package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface FileRepository {

    val downloads: StateFlow<Map<String, DownloadProgress>>

    fun toggleDownload(track: Track)

    fun downloadList(tracks: List<Track>)

    fun cancelAllDownloads()

    fun cancelDownload(trackId: String)

}