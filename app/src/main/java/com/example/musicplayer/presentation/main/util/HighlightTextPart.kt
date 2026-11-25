package com.example.musicplayer.presentation.main.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun buildHighlightedText(
    full: String,
    query: String,
    matchedColor: Color,
    restColor: Color,
): AnnotatedString = buildAnnotatedString {
    val matchStart = if (query.isBlank()) -1
    else full.indexOf(query, ignoreCase = true)

    if (matchStart == -1) {
        withStyle(SpanStyle(color = restColor)) { append(full) }
        return@buildAnnotatedString
    }

    val matchEnd = matchStart + query.length

    withStyle(SpanStyle(color = restColor)) {
        append(full.substring(0, matchStart))
    }
    withStyle(SpanStyle(color = matchedColor)) {
        append(full.substring(matchStart, matchEnd))
    }
    withStyle(SpanStyle(color = restColor)) {
        append(full.substring(matchEnd))
    }
}