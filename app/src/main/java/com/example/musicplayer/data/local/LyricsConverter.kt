package com.example.musicplayer.data.local

import androidx.room.TypeConverter
import com.example.musicplayer.domain.model.LyricLine
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


class LyricsConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromLyrics(lines: List<LyricLine>): String {
        return gson.toJson(lines)
    }

    @TypeConverter
    fun toLyrics(json: String): List<LyricLine> {
        val type = object : TypeToken<List<LyricLine>>() {}.type
        return gson.fromJson(json, type)
    }
}