package com.example.musicplayer.presentation.main.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState


@Composable
fun PlaylistView(
    playList: PlayList,
    playerState: PlayerUiState,
    onPlayPauseClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onTrackClick: () -> Unit,
    onTrackInfoClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBackClick: () -> Unit,
    onBitmapLoaded: (Bitmap) -> Unit,
) {

    val imageColors = playerState.paletteState[playList.id]

    val currentBgColor = imageColors?.first ?: DeepBlack

    Log.d("Playlist", "playlist: $playList")

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            currentBgColor,
            DeepBlack,
        )
    )


    val hazeState = remember { HazeState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(
                    brush = bgGradient,
                )
                .padding(top = 20.dp)
        ) {
            BackButton(
                hazeState = hazeState,
                onClick = {
                    onBackClick()
                }
            )
            PlaylistHat(
                playlistImageUrl = playList.imageUrl ?: "",
                creatorAvatarUrl = "", // доделать
                playlistName = playList.name,
                creatorName = playList.creatorName ?: "",
                playlistDuration = playList.duration ?: "0 min",
                onBitmapLoaded = { bitmap ->
                    onBitmapLoaded(bitmap)
                },
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PlaylistActions(
            onDownloadClick = onDownloadClick,
            onShuffleClick = onShuffleClick,
            onPlayPauseClick = onShuffleClick,
            isShuffled = true,
            isPlaying = true,
        )
    }
}

@Composable
private fun PlaylistHat(
    playlistImageUrl: String,
    creatorAvatarUrl: String,
    playlistName: String,
    creatorName: String,
    playlistDuration: String,
    onBitmapLoaded: (Bitmap) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = playlistImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f),
                onSuccess = { successResult ->
                    val bitmap = successResult.result.drawable.toBitmap()
                    onBitmapLoaded(bitmap)
                }
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = playlistName,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = White,
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .basicMarquee()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            AsyncImage(
                model = creatorAvatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .clip(
                        shape = CircleShape,
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = creatorName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_planet),
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playlistDuration,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Gray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }
    }
}

@Composable
private fun PlaylistActions(
    onDownloadClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    isShuffled: Boolean,
    isPlaying: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(25.dp)
                .clip(
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = Gray,
                    shape = CircleShape,
                )
                .clickable {
                    onDownloadClick()
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = Gray,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_random),
            contentDescription = null,
            tint = if (isShuffled) SpotifyGreen else White,
            modifier = Modifier
                .clickable {
                    onShuffleClick()
                }
        )
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
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
                    .width(16.dp)
                    .height(20.dp)
            )
        }
    }
}