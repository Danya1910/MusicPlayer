package com.example.musicplayer.presentation.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.SpotifyGreen

@Composable
fun NextButton(
    isClickable: Boolean,
    onClick: () -> Unit,
) {

    val color by animateColorAsState(
        targetValue = if (isClickable) SpotifyGreen else Color(0xFF535353),
        animationSpec = tween(300),
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(42.dp)
                .width(82.dp)
                .clip(
                    shape = RoundedCornerShape(21.dp)
                )
                .background(
                    color = color,
                )
                .clickable(
                    enabled = isClickable,
                    onClick = onClick,
                )

        ) {
            Text(
                text = "Next",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Black,
            )
        }
    }
}