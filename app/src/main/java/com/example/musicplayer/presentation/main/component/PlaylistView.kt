package com.example.musicplayer.presentation.main.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.util.PlaylistUi
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState


@Composable
fun PlaylistView(
    playList: PlaylistUi,
    tracks: List<Track>,
    playerState: PlayerUiState,
    onPlayPauseClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onTrackClick: () -> Unit,
    onTrackInfoClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBackClick: () -> Unit,
    onBitmapLoaded: (Bitmap) -> Unit,
) {

    val imageColors = playerState.paletteState[playList.playlist.id]

    val currentBgColor = imageColors?.first ?: DeepBlack

    Log.d("Playlist", "playlist: $playList")

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            currentBgColor,
            DeepBlack,
        )
    )

    val hazeState = remember { HazeState() }

    var isImageExpanded by remember { mutableStateOf(false) }

    val imageScale by animateFloatAsState(
        targetValue = if (isImageExpanded) 1.4f else 1f,
        animationSpec = tween(durationMillis = 450)
    )

    val imageOffsetY by animateFloatAsState(
        targetValue = if (isImageExpanded) 300f else 0f,
        animationSpec = tween(durationMillis = 450)
    )

    val uiElementsAlpha by animateFloatAsState(
        targetValue = if (isImageExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = 350)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if(isImageExpanded) {
                        isImageExpanded = false
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(brush = bgGradient)
                    .statusBarsPadding()
                    .padding(top = 20.dp)
            ) {
                Box(modifier = Modifier.zIndex(3f)) {
                    BackButton(
                        hazeState = hazeState,
                        onClick = {
                            if (isImageExpanded)
                                isImageExpanded = false
                            else
                                onBackClick()
                        }
                    )
                }
                PlaylistHat(
                    playlistImageUrl = playList.playlist.imageUrl ?: "",
                    creatorAvatarUrl = "",
                    playlistName = playList.playlist.name,
                    creatorName = playList.playlist.creatorName ?: "",
                    playlistDuration = playList.playlist.duration ?: "0 min",
                    onBitmapLoaded = onBitmapLoaded,
                    imageScale = imageScale,
                    imageOffsetY = imageOffsetY,
                    isImageExpanded = isImageExpanded,
                    onImageClick = { isImageExpanded = !isImageExpanded },
                    uiElementsAlpha = uiElementsAlpha,
                )
            }
            Column(
                modifier = Modifier.alpha(uiElementsAlpha)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                PlaylistActions(
                    onDownloadClick = onDownloadClick,
                    onShuffleClick = onShuffleClick,
                    onPlayPauseClick = onShuffleClick,
                    isShuffled = true,
                    isPlaying = true,
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn {
                    itemsIndexed(
                        items = tracks,
                        key = { index, track -> "${track.id}_$index" }
                    ) { index, track ->
                        TrackItem(
                            track = track,
                            onTrackClick = {},
                            onMoreClick = {}
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isImageExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 400)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 200)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 50.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Change cover image",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
                    .padding(8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isImageExpanded = false
                    }
            )
        }
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
    imageScale: Float,
    imageOffsetY: Float,
    isImageExpanded: Boolean,
    onImageClick: () -> Unit,
    uiElementsAlpha: Float,
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
                .graphicsLayer(
                    scaleX = imageScale,
                    scaleY = imageScale,
                    translationY = imageOffsetY * 2.5f
                )
                .zIndex(if (isImageExpanded) 2f else 0f)
        ) {
            AsyncImage(
                model = playlistImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .clip(
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onImageClick() },
                onSuccess = { successResult ->
                    val bitmap = successResult.result.drawable.toBitmap()
                    onBitmapLoaded(bitmap)
                }
            )
        }
        Column(
            modifier = Modifier
                .alpha(uiElementsAlpha)
        ) {


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