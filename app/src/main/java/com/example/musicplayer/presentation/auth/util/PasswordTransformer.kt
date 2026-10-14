package com.example.musicplayer.presentation.auth.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class AsteriskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val masked = "*".repeat(text.text.length)
        return TransformedText(
            text = AnnotatedString(masked),
            offsetMapping = OffsetMapping.Identity,
        )
    }
}