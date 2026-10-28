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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild


@Composable
fun TrackInfo(
    track: Track,
    hazeState: HazeState,
    playNext: () -> Unit,
    addToPlayList: () -> Unit,
) {

    val actionsList = listOf(
        ActionButtonData(
            icon = R.drawable.ic_share,
            title = "Share",
            onClick = {}
        ),
        ActionButtonData(
            icon = R.drawable.ic_circle_plus,
            title = "Add to playlist",
            onClick = addToPlayList
        ),
        ActionButtonData(
            icon = R.drawable.ic_apple_logo,
            title = "Add to Liked Songs",
            onClick = {}
        ),
        ActionButtonData(
            icon = R.drawable.ic_add_queue,
            title = "Add to Queue",
            onClick = playNext
        ),
        ActionButtonData(
            icon = R.drawable.ic_album,
            title = "Go to album",
            onClick = {}
        ),
        ActionButtonData(
            icon = R.drawable.ic_artist,
            title = "Go to artist",
            onClick = {  }
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .clip(
                shape = RoundedCornerShape(40.dp)
            )
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
            .padding(top = 5.dp)
    ) {
        CurrentTrack(
            track = track,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Gray
                )
        )

        actionsList.forEach { actionButtonData ->
            ActionButton(
                data = actionButtonData
            )
        }
    }
}

@Composable
private fun CurrentTrack(
    track: Track,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        AsyncImage(
            model = track.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = track.artist,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Gray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }
    }
}

@Composable
private fun ActionButton(
    data: ActionButtonData,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable {
                data.onClick()
            }
            .padding(horizontal = 15.dp)
    ) {
        Icon(
            painter = painterResource(data.icon),
            contentDescription = null,
            tint = Gray,
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = data.title,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = White,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
        )
    }
}

data class ActionButtonData(
    val icon: Int,
    val title: String,
    val onClick: () -> Unit,
)