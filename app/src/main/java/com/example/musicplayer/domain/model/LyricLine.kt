package com.example.musicplayer.domain.model


data class Lyrics(
    val id: String = "",
    val lines: List<LyricLine> = emptyList(),
)

data class LyricLine(
    val line: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
)