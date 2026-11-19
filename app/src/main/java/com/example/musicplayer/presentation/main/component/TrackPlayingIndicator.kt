package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.ui.theme.SpotifyGreen


@Composable
fun TrackPlayingIndicator(
    isPlaying: Boolean,
    modifier: Modifier
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .height(20.dp),
    ) {
        repeat(3) { index ->

            val infiniteTransition = rememberInfiniteTransition(
                label = "equalizer_$index"
            )

            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 400 + index * 100,
                        delayMillis = index * 80,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
            )

            val height = if(isPlaying) {
                animatedHeight * 16f
            } else 2f

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(height.dp)
                    .background(
                        color = SpotifyGreen,
                    )
            )

        }
    }

}