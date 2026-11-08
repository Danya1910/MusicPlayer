package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.util.PlaylistUi
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White

private const val FAVORITES_ID = "favorites"

@Composable
fun AddTrackToPlayListWidget(
    playLists: List<PlayList>,
    favoritesUi: PlaylistUi,
    currentTrackToSave: Track,
    onCancelClick: () -> Unit,
    onDoneClick: () -> Unit,
    onCreateClick: () -> Unit,
    onAddInPlayListClick: (String) -> Unit,
    onRemoveFromPlayList: (String) -> Unit,
) {

    val actualPlayLists = remember(playLists, favoritesUi) {
        playLists.map { pl ->
            if (pl.id == FAVORITES_ID) favoritesUi.playlist else pl
        }
    }

    val initialPlaylistIdsWithTrack = remember(currentTrackToSave.id, actualPlayLists) {
        actualPlayLists
            .filter { it.tracks?.contains(currentTrackToSave.id) == true }
            .mapNotNull { it.id }
    }

    val wasTrackInAnyPlaylist = initialPlaylistIdsWithTrack.isNotEmpty()

    val bottomPlaylists = remember(actualPlayLists, initialPlaylistIdsWithTrack) {
        actualPlayLists.filter { !initialPlaylistIdsWithTrack.contains(it.id) }
    }

    val topPlaylists = remember(actualPlayLists, initialPlaylistIdsWithTrack) {
        actualPlayLists.filter { initialPlaylistIdsWithTrack.contains(it.id) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding()
                .clip(
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = DarkGray
                )
                .padding(top = 10.dp)
        ) {
            DragHandle()
            Spacer(modifier = Modifier.height(15.dp))
            ActionButtons(
                onCancelClick = onCancelClick,
                onDoneClick = onDoneClick,
            )
            Spacer(modifier = Modifier.height(25.dp))
            CreatePlayList(
                onCreateClick = onCreateClick,
            )
            if (wasTrackInAnyPlaylist) {
                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                    items(
                        items = topPlaylists,
                        key = { it.id ?: it.name }
                    ) { playList ->
                        PlayListItem(
                            playList = playList,
                            currentTrackId = currentTrackToSave.id,
                            onAddInPlayListClick = {
                                playList.id?.let { onAddInPlayListClick(it) }
                            },
                            onRemoveFromPlayList = {
                                playList.id?.let { onRemoveFromPlayList(it) }
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Gray
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                items(
                    items = bottomPlaylists,
                    key = { it.id ?: it.name }
                ) { playList ->
                    PlayListItem(
                        playList = playList,
                        currentTrackId = currentTrackToSave.id,
                        onAddInPlayListClick = {
                            playList.id?.let { onAddInPlayListClick(it) }
                        },
                        onRemoveFromPlayList = {
                            playList.id?.let { onRemoveFromPlayList(it) }
                        },
                    )
                }
            }

        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 60.dp
                )
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = SpotifyGreen
                )
                .padding(
                    horizontal = 28.dp,
                    vertical = 14.dp
                )
                .clickable {
                    onDoneClick()
                }
        ) {
            Text(
                text = "Done",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Black,
            )
        }
    }

}

@Composable
private fun DragHandle() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(5.dp)
                .fillMaxWidth(0.25f)
                .clip(
                    shape = RoundedCornerShape(21.dp)
                )
                .background(
                    color = Gray,
                )
        )
    }
}

@Composable
private fun ActionButtons(
    onCancelClick: () -> Unit,
    onDoneClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Cancel",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = White,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCancelClick()
                }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Done",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = White,
            modifier = Modifier
                .padding(vertical = 7.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDoneClick()
                }
        )
    }

}

@Composable
private fun CreatePlayList(
    onCreateClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Save in",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = White,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "New playlist",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = SpotifyGreen,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCreateClick()
                }
        )
    }
}

@Composable
private fun PlayListItem(
    playList: PlayList,
    currentTrackId: String,
    onAddInPlayListClick: () -> Unit,
    onRemoveFromPlayList: () -> Unit,
) {

    val songsCount = playList.tracks?.size?.toSongsString()

    // Источник правды — сам playList.tracks (уже актуальный, т.к. favorites подменён).
    val isInPlayList = playList.tracks?.contains(currentTrackId) == true

    // Локальный оптимистичный стейт: нужен, чтобы галочка переключалась
    // мгновенно, до прихода обновления из БД.
    // Ключи (playList.id, currentTrackId, isInPlayList) гарантируют,
    // что при обновлении playList из БД стейт синхронизируется.
    var isAdded by remember(playList.id, currentTrackId, isInPlayList) {
        mutableStateOf(isInPlayList)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable {
                if (isAdded) onRemoveFromPlayList() else onAddInPlayListClick()
                isAdded = !isAdded
            }
    ) {
        if (playList.id == FAVORITES_ID) {
            Icon(
                painter = painterResource(R.drawable.ic_liked_songs),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(70.dp)
                    .clip(
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        } else {
            AsyncImage(
                model = playList.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 15.dp)
        ) {
            Text(
                text = playList.name,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = songsCount ?: "",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = Gray,
            )
        }
        AnimatedContent(
            targetState = isAdded,
            transitionSpec = {
                val springSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )

                (fadeIn(animationSpec = tween(220)) +
                        scaleIn(initialScale = 0.5f, animationSpec = springSpec)) togetherWith
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
                tint = if (favoriteState) Color.Unspecified else Gray,
                modifier = Modifier
                    .size(36.dp)
                    .padding(6.dp)
            )
        }
    }

}

fun Int.toSongsString(): String {
    return if (this == 1) "1 song" else "$this songs"
}