package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.util.glassEffect
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueListSheet(
    currentTrack: Track?,
    upcomingTracks: List<Track>,
    onDismissRequest: () -> Unit,
    onTrackClick: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DeepBlack.copy(alpha = 0.9f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Gray
            )
        },
        modifier = Modifier
            .padding(10.dp)
            .clip(
                shape = RoundedCornerShape(40.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                item {
                    Column() {
                        Text(
                            text = "Queue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
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
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    currentTrack?.let {
                        CurrentPlayingTrack(
                            track = currentTrack,
                            onPlayPauseClick = {},
                            isPlaying = true,
                        )
                    }
                }

                if (!upcomingTracks.isEmpty()) {
                    items(upcomingTracks) { track ->
                        TrackInQueue(
                            track = track,
                        )
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
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
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
        )
    }
}