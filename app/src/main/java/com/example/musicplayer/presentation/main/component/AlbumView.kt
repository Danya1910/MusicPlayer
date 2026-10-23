package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@Composable
fun AlbumView(
    album: Album,
    onHeartClick: () -> Unit,
    onCancelClick: () -> Unit,
    onPlayPauseClick: (List<Track>) -> Unit,
    isPlaying: Boolean,
    onDownloadAlbumClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack,
            )
            .statusBarsPadding()
    ) {

        LazyColumn(
            modifier = Modifier.padding(top = 30.dp)
        ) {
            item {
                AlbumImage(
                    url = ""
                )
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
            item {
                AlbumInfo(
                    album = album,
                    onHeartClick = onHeartClick,
                    onPlayPauseClick = {
                        onPlayPauseClick(
                            album.tracks
                        )
                    },
                    isPlaying = isPlaying,
                    onDownloadAlbumClick = onDownloadAlbumClick,
                )
            }

            items(album.tracks) { track ->
                AlbumTrackItem(track = track)
            }

        }

        TopBar(
            title = "Moonlight",
            onBackClick = {}
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(30.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .size(16.dp)
                .clickable {
                    onBackClick()
                }
        )
        Text(
            textAlign = TextAlign.Center,
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
    }

}

@Composable
private fun AlbumImage(
    url: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun AlbumInfo(
    album: Album,
    onHeartClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    isPlaying: Boolean,
    onDownloadAlbumClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 80.dp)
        ) {

            Text(
                text = album.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = album.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = album.artist,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = White,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Album * 2000",
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Gray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_heart),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .clickable{
                            onHeartClick()
                        }
                )
                Spacer(modifier = Modifier.width(30.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(21.dp)
                        .clip(
                            shape = CircleShape,
                        )
                        .clickable{
                            onDownloadAlbumClick()
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        tint = Black,
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_ellipsis),
                    contentDescription = null,
                    tint = Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 18.dp,
                    bottom = 26.dp
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(
                        shape = CircleShape
                    )
                    .background(
                        color = SpotifyGreen
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
                        .height(20.dp)
                        .width(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AlbumTrackItem(
    track: Track,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(58.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(21.dp)
                        .clip(
                            shape = CircleShape
                        )
                        .background(
                            color = SpotifyGreen
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        tint = Black,
                        modifier = Modifier
                            .width(5.dp)
                            .height(3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = track.artist,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Gray,
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_ellipsis),
            contentDescription = null,
            tint = Gray,
        )
    }
}