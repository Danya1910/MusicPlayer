package com.example.musicplayer.domain.model


data class Lyrics(
    val id: String,
    val lines: List<LyricLine>,
)

data class LyricLine(
    val line: String,
    val startTime: Long,
    val endTime: Long,
)