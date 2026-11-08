package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.LyricLine
import com.example.musicplayer.presentation.main.util.formatDuration
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.White


@Composable
fun LyricsFullScreen(
    lyrics: List<LyricLine>,
    title: String,
    artist: String,
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    onCancelClick: () -> Unit,
    onStartStopClick: () -> Unit,
    onLineClick: (Long) -> Unit,
    onSeekFinished: (Long) -> Unit,
    bgColor: Color,
) {

    val listState = rememberLazyListState()

    val headerCount = 1

    var viewportHeightPx by remember { mutableIntStateOf(0) }

    var userScrolled by remember { mutableStateOf(false) }
    var autoScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { inProgress ->
                if (inProgress && !autoScrolling) {
                    userScrolled = true
                }
            }
    }

    val currentLineIndex = remember(lyrics, currentPosition) {
        lyrics.indexOfLast { it.startTime <= currentPosition }
            .coerceAtLeast(0)
    }

    LaunchedEffect(currentLineIndex, viewportHeightPx, lyrics.size) {
        if (userScrolled) return@LaunchedEffect           // пользователь рулит — не трогаем
        if (lyrics.isEmpty() || viewportHeightPx == 0) return@LaunchedEffect

        autoScrolling = true
        try {
            val targetIndex = currentLineIndex + headerCount
            val centerOffset = viewportHeightPx / 2
            listState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = -centerOffset
            )
        } finally {
            autoScrolling = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = bgColor,
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 100.dp),
                modifier = Modifier
                    .weight(1f)
                    .onSizeChanged { viewportHeightPx = it.height }
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        val headPx = 80.dp.toPx()
                        val fadeEndPx = headPx + 60.dp.toPx()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                (headPx / size.height) to Color.Transparent,
                                (fadeEndPx / size.height) to Color.Black,
                                ((size.height - 80.dp.toPx()) / size.height) to Color.Black,
                                1f to Color.Transparent,
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
            ) {

                items(
                    items = lyrics,
                    key = { it.startTime }
                ) { line ->
                    val isPlayed = currentPosition >= line.startTime
                    Text(
                        text = line.line,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = if (isPlayed) White else Black,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                userScrolled = false
                                onLineClick(line.startTime)
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            PlayerSlider(
                currentPosition = currentPosition,
                duration = duration,
                onSeekFinished = { ms ->
                    onSeekFinished(ms)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                bgColor = bgColor,
            )
            Spacer(modifier = Modifier.height(5.dp))
            PlayButton(
                onStartStopClick = onStartStopClick,
                isPlaying = isPlaying,
                color = bgColor
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
        Head(
            onCancelClick = onCancelClick,
            title = title,
            artist = artist,
            bgColor = bgColor,
        )
    }

}

@Composable
private fun Head(
    onCancelClick: () -> Unit,
    title: String,
    artist: String,
    bgColor: Color,
) {

    val gradient = Brush.verticalGradient(
        colors = listOf(
            bgColor,
            bgColor.copy(alpha = 0.5f),
            bgColor.copy(alpha = 0.2f),
            bgColor.copy(alpha = 0.1f),
        )
    )


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(
                brush = gradient,
            )
            .padding(horizontal = 26.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tick_down),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCancelClick()
                }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Text(
                textAlign = TextAlign.Center,
                text = artist,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()

            )
        }
        Box(
            modifier = Modifier
                .size(21.dp)
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
    bgColor: Color,
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

    val isLightBackground = bgColor.luminance() > 0.5f

    val trackBase = if (isLightBackground) Color.Black else Color.White

    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.Transparent,
        activeTrackColor = trackBase.copy(alpha = 0.55f),
        inactiveTrackColor = trackBase.copy(alpha = 0.18f),
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
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    colors = sliderColors.copy(
                        thumbColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .size(12.dp)
                        .minimumInteractiveComponentSize()
                )
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
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = sliderColors.activeTrackColor,
                        start = Offset(0f, centerY),
                        end = Offset(progressX, centerY),
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
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
                color = sliderColors.activeTrackColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (showTotalDuration)
                    formatDuration(ms = duration)
                else {
                    val remainingTime = duration - currentPosition
                    "-${formatDuration(ms = remainingTime)}"
                },
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = sliderColors.activeTrackColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showTotalDuration = !showTotalDuration
                    }
            )
        }
    }
}

@Composable
private fun PlayButton(
    onStartStopClick: () -> Unit,
    isPlaying: Boolean,
    color: Color,
) {

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
                    onStartStopClick()
                }
        ) {
            Icon(
                painter = painterResource(
                    if (isPlaying) R.drawable.ic_pause
                    else R.drawable.ic_play
                ),
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .width(21.dp)
                    .height(26.dp)
            )
        }
    }
}