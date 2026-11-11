package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@Composable
fun TrackItem(
    track: Track,
    onTrackClick: () -> Unit,
    onMoreClick: () -> Unit,
    toggleFavorite: () -> Unit,
    onDownloadClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onTrackClick()
            }
            .padding(horizontal = 12.dp)
    ) {
        AsyncImage(
            model = track.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = track.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = track.artist,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB3B3B3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (track.localAudioPath != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_load),
                contentDescription = null,
                tint = SpotifyGreen,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        onDownloadClick()
                    }
            )
        }
        if (track.isFavorite) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_tick_circle),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        toggleFavorite()
                    }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(R.drawable.ic_ellipsis_space),
            contentDescription = null,
            tint = Color(0xFFB3B3B3),
            modifier = Modifier
                .clickable {
                    onMoreClick()
                }
        )
    }

}

@Composable
private fun LoadButtonWithRing(
    isLoaded: Boolean,
    progress: DownloadProgress?,
    onLoadClick: () -> Unit,
) {
    val isDownloading = progress is DownloadProgress.InProgress

    val shouldShow = isLoaded || isDownloading

    val percent = (progress as? DownloadProgress.InProgress)?.percent ?: -1

    val animatedProgress by animateFloatAsState(
        targetValue = if (percent >= 0) percent / 100f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "downloadProgress",
    )

    val infiniteRotation = rememberInfiniteTransition(label = "spin").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
        ),
        label = "rotation",
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .alpha(if (shouldShow) 1f else 0f)
            .then(
                if (shouldShow) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onLoadClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (shouldShow) {
            Icon(
                painter = painterResource(R.drawable.ic_load),
                contentDescription = null,
                tint = when {
                    isLoaded -> SpotifyGreen
                    else -> Gray
                },
                modifier = Modifier
                    .size(36.dp)
                    .padding(6.dp),
            )

            if (isDownloading) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val iconPadding = 6.5.dp.toPx()
                    val stroke = 2.dp.toPx()
                    val inset = iconPadding + stroke / 2
                    val arcSize = Size(
                        width = size.width - inset * 2,
                        height = size.height - inset * 2,
                    )
                    val topLeft = Offset(inset, inset)

                    drawArc(
                        color = Gray.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke),
                    )

                    if (percent >= 0) {
                        drawArc(
                            color = SpotifyGreen,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    } else {
                        rotate(infiniteRotation.value) {
                            drawArc(
                                color = SpotifyGreen,
                                startAngle = 0f,
                                sweepAngle = 90f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                            )
                        }
                    }
                }
            }
        }
    }
}