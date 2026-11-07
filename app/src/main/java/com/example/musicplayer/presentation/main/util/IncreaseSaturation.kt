package com.example.musicplayer.presentation.main.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor

fun Color.increaseSaturation(
    saturationFactor: Float = 1.3f,
    valueFactor: Float = 1.3f,
    minValue: Float = 0f
): Color {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(this.toArgb(), hsv)
    hsv[1] = (hsv[1] * saturationFactor).coerceIn(0f, 1f)
    hsv[2] = (hsv[2] * valueFactor).coerceIn(0f, 1f).coerceAtLeast(minValue)
    return Color(AndroidColor.HSVToColor(hsv))
}