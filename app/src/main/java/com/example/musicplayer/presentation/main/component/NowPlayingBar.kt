package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@Composable
fun NowPlayingBar(
    track: Track,
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .height(60.dp)
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Gray.copy(alpha = 0.6f),
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    bottom = 10.dp,
                    end = 37.dp
                )
        ) {
            AsyncImage(
                model = track.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(37.dp)
                    .clip(
                        shape = RoundedCornerShape(7.dp)
                    )
            )
            Spacer(modifier = Modifier.width(11.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title + " · " + track.artist,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,//добавить прокрутку
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bluetooth),
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BEATSPILL +",
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = SpotifyGreen,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_bluetooth_bold),
                contentDescription = null,
                tint = SpotifyGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(R.drawable.ic_pause),
                contentDescription = null,
                tint = White,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        ) {
            TrackProgress()
        }
    }
}

@Composable
private fun TrackProgress() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .padding(horizontal = 10.dp)
            .background(
                color = DarkGray,
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(3.dp)
                .background(
                    color = Gray,
                )
        )
    }

}