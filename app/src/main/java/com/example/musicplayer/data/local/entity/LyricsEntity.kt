package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.domain.model.LyricLine
import com.example.musicplayer.domain.model.Lyrics

@Entity(tableName = "lyrics")
data class LyricsEntity(
    @PrimaryKey
    val trackId: String,
    val lines: List<LyricLine>
)

fun LyricsEntity.toDomain(): Lyrics {
    return Lyrics(
        id = trackId,
        lines = lines,
    )
}

fun Lyrics.toEntity(): LyricsEntity {
    return LyricsEntity(
        trackId = id,
        lines = lines,
    )
}