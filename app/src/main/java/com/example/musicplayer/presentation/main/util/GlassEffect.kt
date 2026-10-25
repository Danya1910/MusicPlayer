package com.example.musicplayer.presentation.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White

fun Modifier.glassEffect(
    blurRadius: Dp = 20.dp,
    glassColor: Color = DeepBlack.copy(alpha = 0.1f),
    borderColor: Color = DeepBlack.copy(alpha = 0.15f),
): Modifier = this
    .blur(radius = blurRadius)
    .background(
        color = glassColor
    )
    .border(
        width = 0.5.dp,
        color = borderColor,
    )