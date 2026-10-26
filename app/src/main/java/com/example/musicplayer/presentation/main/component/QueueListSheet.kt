package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueListSheet(
    currentTrack: Track?,
    upcomingTracks: List<Track>,
    onDismissRequest: () -> Unit,
    onTrackClick: (Track) -> Unit,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onMoveTrack: (fromIndex: Int, toIndex: Int) -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    var localUpcomingTracks by remember(upcomingTracks) { mutableStateOf(upcomingTracks) }

    var draggingIndex by remember { mutableStateOf(-1) }

    var itemTranslationY by remember { mutableStateOf(0f) }


    val density = LocalDensity.current

    val itemHeightPx = remember(density) { with(density) { 58.dp.toPx() } }

    LaunchedEffect(Unit) {
        sheetState.partialExpand()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DeepBlack,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Gray
            )
        },
        modifier = Modifier
            .padding(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
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
                        items = localUpcomingTracks,
                        key = { index, track -> "${track.id}_$index" }
                    ) { index, track ->
                        val isCurrentDragging = draggingIndex == index
                        Box(
                            modifier = Modifier
                                .animateItem()
                                .graphicsLayer {
                                    translationY = if (isCurrentDragging) itemTranslationY else 0f

                                    scaleX = if (isCurrentDragging) 1.05f else 1f
                                    scaleY = if (isCurrentDragging) 1.05f else 1f
                                    shadowElevation = if (isCurrentDragging) 20f else 0f

                                }
                                .background(
                                    color = if (isCurrentDragging) White.copy(alpha = 0.08f)
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            TrackInQueue(
                                track = track,
                                onTrackClick = { onTrackClick(track) },
                                dragHandleModifier = Modifier.pointerInput(index) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggingIndex = index
                                            itemTranslationY = 0f
                                        },
                                        onDragEnd = {
                                            if (draggingIndex != -1) {
                                                val indexOffset = (itemTranslationY / itemHeightPx).roundToInt()
                                                val targetIndex = (draggingIndex + indexOffset)
                                                    .coerceIn(0, localUpcomingTracks.lastIndex)

                                                if (targetIndex != draggingIndex) {
                                                    val fromList = localUpcomingTracks.toMutableList()
                                                    val movedItem = fromList.removeAt(draggingIndex)
                                                    fromList.add(targetIndex, movedItem)
                                                    localUpcomingTracks = fromList

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