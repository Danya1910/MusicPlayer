package com.example.musicplayer.data.wrapper

import com.example.musicplayer.data.dto.TrackDto
import com.example.musicplayer.domain.model.Track


fun TrackDto.toDomain() : Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        duration = duration,
        isFavorite = isFavorite,
        localAudioPath = localAudioPath,
    )
}

fun Track.toDto() : TrackDto {
    return TrackDto(
        id = id,
        title = title,
        artist = artist,
        audioUrl = audioUrl,
        imageUrl = imageUrl,
        duration = duration,
        isFavorite = isFavorite,
        localAudioPath = localAudioPath,
    )
}