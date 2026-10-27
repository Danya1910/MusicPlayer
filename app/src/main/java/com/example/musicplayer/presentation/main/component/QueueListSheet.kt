package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueListSheet(
    currentTrack: Track?,
    upcomingTracks: List<Track>,
    currentQueue: List<Track>,
    onTrackClick: (Track) -> Unit,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onMoveTrack: (fromIndex: Int, toIndex: Int) -> Unit,
    removeFromQueue: (Int) -> Unit,
    hazeState: HazeState,
) {

    var localFullQueue by remember(currentQueue) {
        mutableStateOf(currentQueue.map { QueueItem(track = it) })
    }

    val currentPlayingIndex = remember(currentTrack, currentQueue) {
        currentQueue.indexOfFirst { it.id == currentTrack?.id }.coerceAtLeast(0)
    }

    var draggingIndex by remember { mutableStateOf(-1) }

    var itemTranslationY by remember { mutableStateOf(0f) }


    val density = LocalDensity.current

    val itemHeightPx = remember(density) { with(density) { 58.dp.toPx() } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .clip(shape = RoundedCornerShape(40.dp))
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
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(40.dp)
            )
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(top = 10.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        color = White,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Playing + ${currentTrack?.title}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Gray,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                currentTrack?.let {
                    CurrentPlayingTrack(
                        track = currentTrack,
                        onPlayPauseClick = onPlayPauseClick,
                        isPlaying = isPlaying,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
            }

            if (!upcomingTracks.isEmpty()) {
                itemsIndexed(
                    items = localFullQueue,
                    key = { _, item -> item.id }
                ) { globalIndex, item ->

                    if (globalIndex > currentPlayingIndex) {

                        val maxTargetOffsetPx = with(density) { 80.dp.toPx() }

                        val dragState = remember(item.id) {
                            AnchoredDraggableState(
                                initialValue = "Center",
                                positionalThreshold = { distance -> distance * 0.5f },
                                velocityThreshold = { with(density) { 100.dp.toPx() } },
                                snapAnimationSpec = tween(durationMillis = 250),
                                decayAnimationSpec = androidx.compose.animation.core.Animatable(0f).run {
                                    androidx.compose.animation.core.exponentialDecay()
                                }
                            )
                        }.apply {
                            updateAnchors(
                                DraggableAnchors {
                                    "Center" at 0f
                                    "Left" at -maxTargetOffsetPx
                                    "Dismissed" at -with(density) { 150.dp.toPx() }
                                }
                            )
                        }

                        LaunchedEffect(dragState.currentValue) {
                            if (dragState.currentValue == "Dismissed") {
                                val fromList = localFullQueue.toMutableList()
                                val actualGlobalIndex = fromList.indexOfFirst { it.id == item.id }
                                if (actualGlobalIndex != -1) {
                                    fromList.removeAt(actualGlobalIndex)
                                    localFullQueue = fromList
                                    removeFromQueue(actualGlobalIndex)
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .animateItem()
                                .anchoredDraggable(state = dragState, orientation = Orientation.Horizontal)
                        ) {
                            val swipeWidthPx = (-dragState.offset).coerceAtLeast(0f)
                            val swipeWidthDp = with(density) { swipeWidthPx.toDp() }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(swipeWidthDp)
                                    .fillMaxHeight()
                                    .background(
                                        color = Color(0xFFFF0000),
                                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                                    )
                                    .clickable {
                                        val fromList = localFullQueue.toMutableList()
                                        val actualGlobalIndex = fromList.indexOfFirst { it.id == item.id }
                                        if (actualGlobalIndex != -1) {
                                            fromList.removeAt(actualGlobalIndex)
                                            localFullQueue = fromList
                                            removeFromQueue(actualGlobalIndex)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (swipeWidthPx > with(density) { 24.dp.toPx() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_trash),
                                        contentDescription = "Delete",
                                        tint = Black,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset { IntOffset(dragState.offset.roundToInt(), 0) }
                                    .graphicsLayer {
                                        val currentTrackIndex = localFullQueue.indexOfFirst { it.id == item.id }
                                        val isCurrentDraggingItem = draggingIndex == currentTrackIndex

                                        translationY = if (isCurrentDraggingItem) itemTranslationY else 0f
                                        scaleX = if (isCurrentDraggingItem) 1.02f else 1f
                                        scaleY = if (isCurrentDraggingItem) 1.02f else 1f
                                        shadowElevation = if (isCurrentDraggingItem) 15f else 0f
                                    }
                                    .background(
                                        color = if (draggingIndex == localFullQueue.indexOfFirst { it.id == item.id })
                                            White.copy(alpha = 0.08f) else DeepBlack.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                TrackInQueue(
                                    track = item.track,
                                    onTrackClick = { onTrackClick(item.track) },
                                    dragHandleModifier = Modifier.pointerInput(item.id) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggingIndex = localFullQueue.indexOfFirst { it.id == item.id }
                                                itemTranslationY = 0f
                                            },
                                            onDragEnd = {
                                                if (draggingIndex != -1) {
                                                    val indexOffset = (itemTranslationY / itemHeightPx).roundToInt()
                                                    val targetIndex = (draggingIndex + indexOffset)
                                                        .coerceIn(currentPlayingIndex + 1, localFullQueue.lastIndex)

                                                    if (targetIndex != draggingIndex) {
                                                        val fromList = localFullQueue.toMutableList()
                                                        val movedItem = fromList.removeAt(draggingIndex)
                                                        fromList.add(targetIndex, movedItem)
                                                        localFullQueue = fromList
                                                        onMoveTrack(draggingIndex, targetIndex)
                                                    }
                                                }
                                                draggingIndex = -1
                                                itemTranslationY = 0f
                                            },
                                            onDragCancel = {
                                                draggingIndex = -1
                                                itemTranslationY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                itemTranslationY += dragAmount.y
                                            }
                                        )
                                    },
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun CurrentPlayingTrack(
    track: Track,
    onPlayPauseClick: () -> Unit,
    isPlaying: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 2.5.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        AsyncImage(
            model = track.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(
                    shape = RoundedCornerShape(7.dp)
                )
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = 3.dp
                )
                .padding(end = 15.dp)
        ) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = SpotifyGreen,
            )
            Text(
                text = track.artist,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Gray,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(35.dp)
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = White,
                )
                .clickable {
                    onPlayPauseClick()
                }
        ) {
            Icon(
                painter = painterResource(
                    if (isPlaying) R.drawable.ic_pause
                    else R.drawable.ic_play
                ),
                contentDescription = null,
                tint = Black,
                modifier = Modifier
                    .height(16.dp)
                    .width(13.dp)
            )
        }
    }
}

@Composable
private fun TrackInQueue(
    track: Track,
    onTrackClick: () -> Unit,
    dragHandleModifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onTrackClick()
            }
    ) {
        AsyncImage(
            model = track.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(
                    shape = RoundedCornerShape(7.dp)
                )
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = 3.dp
                )
                .padding(end = 15.dp)
        ) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Text(
                text = track.artist,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Gray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_lines),
            contentDescription = null,
            tint = Gray,
            modifier = dragHandleModifier
                .padding(6.dp),
        )
    }
}

data class QueueItem(
    val id: String = java.util.UUID.randomUUID().toString(), // Уникальный UUID строчки
    val track: Track
)
