package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.domain.model.LyricLine
import com.example.musicplayer.ui.theme.White


@Composable
fun LyricsLinesRow(
    lyrics: List<LyricLine>,
    currentPosition: Long,
) {
    val activeLineIndex = lyrics.indexOfLast {
        currentPosition >= it.startTime
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = activeLineIndex,
            transitionSpec = {
                (slideInVertically(
                    animationSpec = tween(450),
                    initialOffsetY = { it }
                ) + fadeIn(tween(450))) togetherWith
                        (slideOutVertically(
                            animationSpec = tween(450),
                            targetOffsetY = { -it }
                        ) + fadeOut(tween(450)))
            },
        ) { index ->
            Text(
                text = lyrics.getOrNull(index)?.line.orEmpty(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = White
            )
        }
    }

}