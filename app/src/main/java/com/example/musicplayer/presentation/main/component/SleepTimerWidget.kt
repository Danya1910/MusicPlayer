package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild


@Composable
fun SleepTimerWidget(
    onTimeClick: (Int) -> Unit,
    onEndClick: () -> Unit,
    hazeState: HazeState,
) {

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
            .padding(
                top = 10.dp,
                bottom = 16.dp
            )
    ) {
        DragHandle()
        Spacer(modifier = Modifier.height(26.dp))
        Title()
        Spacer(modifier = Modifier.height(20.dp))
        Separator()
        Spacer(modifier = Modifier.height(10.dp))
        Item(
            title = "5 minutes",
            onClick = {
                onTimeClick(5)
            }
        )
        Item(
            title = "10 minutes",
            onClick = {
                onTimeClick(10)
            }
        )
        Item(
            title = "15 minutes",
            onClick = {
                onTimeClick(15)
            }
        )
        Item(
            title = "30 minutes",
            onClick = {
                onTimeClick(30)
            }
        )
        Item(
            title = "45 minutes",
            onClick = {
                onTimeClick(45)
            }
        )
        Item(
            title = "1 hour",
            onClick = {
                onTimeClick(60)
            }
        )
        Item(
            title = "End of track",
            onClick = {
                onEndClick()
            }
        )
    }
}

@Composable
private fun DragHandle() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.2f)
                .height(4.dp)
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = Gray
                )
        )
    }
}

@Composable
private fun Title() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Sleep timer",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = White,
        )
    }
}

@Composable
private fun Separator() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                color = Gray,
            )
    )
}

@Composable
private fun Item(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp
            )
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = White,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
        )
    }
}