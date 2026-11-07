package com.example.musicplayer.presentation.main.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor

fun Color.increaseSaturation(factor: Float = 1.3f): Color {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(this.toArgb(), hsv)
    hsv[1] = (hsv[1] * factor).coerceIn(0f, 1f)
    return Color(AndroidColor.HSVToColor(hsv))
}