package com.example.musicplayer.presentation.main.component

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.util.formatDuration
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@Composable
fun TrackView(
    track: Track,
    onCancelClick: () -> Unit,
    onPauseClick: () -> Unit,
    onHeartClick: () -> Unit,
    playerState: PlayerUiState,
    onBitmapLoaded: (Bitmap) -> Unit,
    isPlaying: Boolean,
    onSkipToPrevious: () -> Unit,
    onSkipToNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onShuffleClick: () -> Unit,
    isShuffled: Boolean,
) {

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            playerState.bgColor,
            DeepBlack,
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = bgGradient,
            )
            .padding(
                horizontal = 24.dp
            )
            .statusBarsPadding()
            .padding(top = 10.dp)
    ) {
        PlayerHead(
            onCancelClick = {
                onCancelClick()
            },
            title = track.title,
        )

        Spacer(modifier = Modifier.weight(1f))

        TrackImage(
            url = track.imageUrl,
            onBitmapLoaded = { bitmap ->
                onBitmapLoaded(bitmap)
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        TrackInfoAndHeart(
            title = track.title,
            artist = track.artist,
            onHeartClick = {},
            titleColor = White,
            bodyColor = White,
        )

        Spacer(modifier = Modifier.height(15.dp))


        PlayerSlider(
            currentPosition = playerState.currentPosition,
            duration = track.duration,
            onSeekFinished = { ms ->
                onSeekTo(ms)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        PlayerControl(
            onPauseClick = {
                onPauseClick()
            },
            isPlaying = isPlaying,
            onSkipToPrevious = onSkipToPrevious,
            onSkipToNext = onSkipToNext,
            onShuffleClick = onShuffleClick,
            isShuffled = isShuffled,
        )
        Spacer(modifier = Modifier.height(60.dp))

    }

}

@Composable
@Preview(showBackground = true)
private fun TopBarPreview() {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(
                DeepBlack,
            )
    ) {
        PlayerHead(
            onCancelClick = {},
            title = "?"
        )
        TrackInfoAndHeart(
            title = "Hope",
            artist = "XXXTENTACION",
            onHeartClick = {},
            titleColor = Black,
            bodyColor = White,
        )
    }
}

@Composable
private fun PlayerHead(
    onCancelClick: () -> Unit,
    title: String,
) {

    Row(
        modifier = Modifier
            .height(30.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tick_down),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .clickable {
                    onCancelClick()
                }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            textAlign = TextAlign.Center,
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_ellipsis),
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(27.dp)
        )
    }

}

@Composable
private fun TrackImage(
    url: String,
    onBitmapLoaded: (Bitmap) -> Unit,

    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .allowHardware(false) // КРИТИЧНО ДЛЯ PALETTE API: разрешает чтение пикселей из памяти
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { successResult ->
                val bitmap = successResult.result.drawable.toBitmap()
                onBitmapLoaded(bitmap)
            }
        )
    }
}

@Composable
private fun TrackInfoAndHeart(
    title: String,
    artist: String,
    onHeartClick: () -> Unit,
    titleColor: Color,
    bodyColor: Color,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 15.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = artist,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = bodyColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(42.dp))
        Icon(
            painter = painterResource(R.drawable.ic_heart),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onHeartClick()
                },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerSlider(
    currentPosition: Long,
    duration: Long,
    onSeekFinished: (Long) -> Unit,
    modifier: Modifier,
) {

    var showTotalDuration by remember { mutableStateOf(true) }


    val totalDurationSeconds = (duration / 1000).coerceAtLeast(0)
    val currentPosSeconds = (currentPosition / 1000).coerceAtLeast(0)

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isUserDragging by remember { mutableStateOf(false) }

    LaunchedEffect(currentPosSeconds, isUserDragging) {
        if (!isUserDragging) {
            sliderPosition = currentPosSeconds.toFloat()
        }
    }

    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = White,
        inactiveTrackColor = Gray
    )

    Column(
        modifier = modifier
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                isUserDragging = true
                sliderPosition = newValue
            },
            onValueChangeFinished = {
                isUserDragging = false
                onSeekFinished(sliderPosition.toLong() * 1000L)
            },
            valueRange = 0f..if (totalDurationSeconds > 0) totalDurationSeconds.toFloat() else 1f,
            colors = sliderColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            thumb = {
                if (totalDurationSeconds > 0) {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        colors = sliderColors,
                        modifier = Modifier
                            .padding(top = 1.5.dp)
                            .size(12.dp)
                            .minimumInteractiveComponentSize()
                    )
                }
            },
            track = { sliderState ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val progress = (sliderState.value - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

                    val width = size.width
                    val height = size.height

                    val strokeWidthPx = 4.dp.toPx()

                    val progressX = width * progress
                    val centerY = height / 2

                    drawLine(
                        color = sliderColors.inactiveTrackColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(width, centerY),
                        strokeWidth = strokeWidthPx,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    drawLine(
                        color = sliderColors.activeTrackColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(progressX, centerY),
                        strokeWidth = strokeWidthPx,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = formatDuration(ms = currentPosition),
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if(showTotalDuration)
                    formatDuration(ms = currentPosition)
                else {
                    val remainingTime = duration - currentPosition
                    "-${formatDuration(ms = remainingTime)}"
                },
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ){
                        showTotalDuration = !showTotalDuration
                    }
            )
        }
    }
}

@Composable
private fun PlayerControl(
    onPauseClick: () -> Unit,
    isPlaying: Boolean,
    onSkipToPrevious: () -> Unit,
    onSkipToNext: () -> Unit,
    onShuffleClick: () -> Unit,
    isShuffled: Boolean,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_random),
            contentDescription = null,
            tint = if(isShuffled) SpotifyGreen else White,
            modifier = Modifier
                .size(22.dp)
                .clickable{
                    onShuffleClick()
                }
        )
        Spacer(modifier = Modifier.width(7.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_track_back),
                contentDescription = null,
                tint = White,
                modifier = Modifier
                    .clickable {
                        onSkipToPrevious()
                    }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(67.dp)
                    .clip(
                        shape = CircleShape
                    )
                    .background(
                        color = White
                    )
                    .clickable {
                        onPauseClick()
                    }
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause
                        else R.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = DeepBlack,
                    modifier = Modifier
                        .width(21.dp)
                        .height(26.dp)
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_track_next),
                contentDescription = null,
                tint = White,
                modifier = Modifier
                    .clickable {
                        onSkipToNext()
                    }
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_repeat),
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(29.dp)
        )
    }

}

