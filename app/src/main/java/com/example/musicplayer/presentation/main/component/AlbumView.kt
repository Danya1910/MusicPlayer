package com.example.musicplayer.presentation.main.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.DownloadProgress
import com.example.musicplayer.domain.model.PlaybackOrigin
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.player.state.PlayerUiState
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumView(
    album: Album,
    onHeartClick: () -> Unit,
    onCancelClick: () -> Unit,
    onPlayClick: (List<Track>) -> Unit,
    onContinuePlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onDownloadAlbumClick: () -> Unit,
    onBitmapLoaded: (Bitmap) -> Unit,
    playerState: PlayerUiState,
    onMoreClick: (Track) -> Unit,
    onTrackClick: (Track) -> Unit,
    toggleFavorite: (Track) -> Unit,
    onDownloadClick: (Track) -> Unit,
    downloads: Map<String, DownloadProgress>,
    currentPlayingOrigin: PlaybackOrigin,
) {

    val hazeState = remember { HazeState() }

    val albumColors = playerState.paletteState[album.id]

    val currentBgColor = albumColors?.first ?: DeepBlack

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            currentBgColor,
            DeepBlack,
        )
    )

    val isAllAlbumLoaded = album.tracks.isNotEmpty() &&
            album.tracks.all { it.localAudioPath != null }

    val origin = currentPlayingOrigin
    val isFromThisAlbum =
        origin is PlaybackOrigin.Album && origin.albumId == album.id

    val isPlayingLocal = isFromThisAlbum && playerState.isPlaying

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = bgGradient,
            )
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                top = 80.dp,
                bottom = 20.dp,
                start = 16.dp,
                end = 16.dp
            )

        ) {
            item {
                AlbumImage(
                    url = album.imageUrl,
                    onBitmapLoaded = {
                        onBitmapLoaded(it)
                    }
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
                        if (isFromThisAlbum && !playerState.isPlaying)
                            onContinuePlayClick()
                        else if (isPlayingLocal)
                            onPauseClick()
                        else onPlayClick(
                            album.tracks
                        )
                    },
                    onDownloadAlbumClick = onDownloadAlbumClick,
                    isPlaying = isPlayingLocal,
                    isAlbumLoaded = isAllAlbumLoaded,
                    isLiked = album.isFavorite
                )
            }

            items(album.tracks) { track ->
                AlbumTrackItem(
                    track = track,
                    onMoreClick = {
                        onMoreClick(track)
                    },
                    onTrackClick = {
                        onTrackClick(track)
                    },
                    toggleFavorite = {
                        toggleFavorite(track)
                    },
                    onDownloadClick = {
                        onDownloadClick(track)
                    },
                    isLoaded = !track.localAudioPath.isNullOrBlank(),
                    downloads = downloads,
                )
            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .align(Alignment.TopCenter)
        ) {
            TopBar(
                title = album.title,
                onBackClick = { onCancelClick() },
                hazeState = hazeState,
            )
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    hazeState: HazeState,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        BackButton(
            onClick = onBackClick,
            hazeState = hazeState
        )
    }

}

@Composable
private fun AlbumImage(
    url: String,
    onBitmapLoaded: (Bitmap) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { successResult ->
                val bitmap = successResult.result.drawable.toBitmap()
                onBitmapLoaded(bitmap)
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
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
    isAlbumLoaded: Boolean,
    isLiked: Boolean,
) {

    val color by animateColorAsState(
        targetValue = if (isAlbumLoaded) SpotifyGreen else Gray,
        animationSpec = tween(durationMillis = 300),
    )

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
                AnimatedContent(
                    targetState = isLiked,
                    transitionSpec = {
                        val springSpec = spring<Float>(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )

                        (fadeIn(animationSpec = tween(220)) +
                                scaleIn(
                                    initialScale = 0.5f,
                                    animationSpec = springSpec
                                )) togetherWith
                                (fadeOut(animationSpec = tween(220)) +
                                        scaleOut(targetScale = 0.5f, animationSpec = tween(220)))
                    },
                ) { favoriteState ->
                    Icon(
                        painter = painterResource(
                            if (favoriteState) R.drawable.ic_tick_circle
                            else R.drawable.ic_circle_plus
                        ),
                        contentDescription = null,
                        tint = if (isLiked) Color.Unspecified else Gray,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(6.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onHeartClick()
                            },
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_load),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onDownloadAlbumClick()
                        }
                )
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
    onTrackClick: () -> Unit,
    toggleFavorite: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoreClick: () -> Unit,
    isLoaded: Boolean,
    downloads: Map<String, DownloadProgress>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(58.dp)
            .fillMaxWidth()
            .clickable {
                onTrackClick()
            }
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

            Text(
                text = track.artist,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Gray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }
        LoadIndicator(
            isLoaded = isLoaded,
            progress = downloads[track.id],
            onLoadClick = onDownloadClick,
        )
        if (track.isFavorite) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_tick_circle),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        toggleFavorite()
                    }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(R.drawable.ic_ellipsis),
            contentDescription = null,
            tint = Gray,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onMoreClick()
                }
        )
    }
}

@Composable
private fun LoadIndicator(
    isLoaded: Boolean,
    progress: DownloadProgress?,
    onLoadClick: () -> Unit,
) {
    val isDownloading = progress is DownloadProgress.InProgress

    val shouldShow = isLoaded || isDownloading

    val percent = (progress as? DownloadProgress.InProgress)?.percent ?: -1

    val animatedProgress by animateFloatAsState(
        targetValue = if (percent >= 0) percent / 100f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "downloadProgress",
    )

    val infiniteRotation = rememberInfiniteTransition(label = "spin").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
        ),
        label = "rotation",
    )

    Box(
        modifier = Modifier
            .size(30.dp)
            .alpha(if (shouldShow) 1f else 0f)
            .then(
                if (shouldShow) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onLoadClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (shouldShow) {
            Icon(
                painter = painterResource(R.drawable.ic_load),
                contentDescription = null,
                tint = when {
                    isLoaded -> SpotifyGreen
                    else -> Gray
                },
                modifier = Modifier
                    .size(30.dp)
                    .padding(5.dp),
            )

            if (isDownloading) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val iconPadding = 5.4.dp.toPx()
                    val stroke = 1.7.dp.toPx()
                    val inset = iconPadding + stroke / 2
                    val arcSize = Size(
                        width = size.width - inset * 2,
                        height = size.height - inset * 2,
                    )
                    val topLeft = Offset(inset, inset)

                    drawArc(
                        color = Gray.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke),
                    )

                    if (percent >= 0) {
                        drawArc(
                            color = SpotifyGreen,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    } else {
                        rotate(infiniteRotation.value) {
                            drawArc(
                                color = SpotifyGreen,
                                startAngle = 0f,
                                sweepAngle = 90f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                            )
                        }
                    }
                }
            }
        }
    }
}