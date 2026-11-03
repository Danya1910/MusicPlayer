package com.example.musicplayer.presentation.main.util

import com.example.musicplayer.domain.model.Track
import java.util.concurrent.TimeUnit

fun getTracksTotalTime(tracks: List<Track>): String {

    val totalMs = tracks.sumOf{ it.duration }

    val hours = TimeUnit.MILLISECONDS.toHours(totalMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMs) % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}min"
        else -> "${minutes}min"
    }
}