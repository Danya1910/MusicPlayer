package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.LyricLine
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.White


@Composable
fun LyricsFullScreen(
    lyrics: List<LyricLine>,
    title: String,
    artist: String,
    currentPosition: Long,
    duration: Long,
    onCancelClick: () -> Unit,
    onStartStopClick: () -> Unit,
    onLineClick: (Long) -> Unit,
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
        Head(
            onCancelClick = onCancelClick,
            title = title,
            artist = artist,
        )
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 50.dp
                )
                .onSizeChanged { viewportHeightPx = it.height }
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
    }

}

@Composable
private fun Head(
    onCancelClick: () -> Unit,
    title: String,
    artist: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = artist,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = White,
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .size(21.dp)
        )
    }

}