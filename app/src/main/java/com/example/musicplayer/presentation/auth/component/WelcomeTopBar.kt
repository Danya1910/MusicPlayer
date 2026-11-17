package com.example.musicplayer.presentation.auth.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

@Composable
fun WelcomeTopBar(
    title: String,
    onClick: () -> Unit,
    hazeState: HazeState,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        BackButton(
            onClick = {
                onClick()
            },
            hazeState = hazeState,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = White,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(50.dp))


    }

}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    hazeState: HazeState
) {

    val gradientColors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
        Color(0xFFFFFFFF).copy(alpha = 0.3f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .clip(
                shape = CircleShape
            )
            .hazeChild(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = DeepBlack,
                    blurRadius = 10.dp,
                    tint = HazeTint(color = DeepBlack.copy(alpha = 0.8f)),
                    noiseFactor = 0.001f
                ),
            )
            .border(
                width = 1.dp,
                shape = CircleShape,
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
            )
            .clickable {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = null,
            tint = White,
        )
    }

}