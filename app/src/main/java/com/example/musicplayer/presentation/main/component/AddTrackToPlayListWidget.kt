package com.example.musicplayer.presentation.main.component

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
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White

@Composable
fun AddTrackToPlayListWidget(
    playLists: List<PlayList>,
    currentTrackToSave: Track,
    onCancelClick: () -> Unit,
    onDoneClick: () -> Unit,
    onCreateClick: () -> Unit,
    onAddInPlayListClick: (String) -> Unit,
    onRemoveFromPlayList: (String) -> Unit,
) {

    val initialPlaylistIdsWithTrack = remember(currentTrackToSave.id) {
        playLists.filter { it.tracks?.contains(currentTrackToSave.id) == true }
            .mapNotNull { it.id }
    }

    val wasTrackInAnyPlaylist = initialPlaylistIdsWithTrack.isNotEmpty()

    val bottomPlaylists = playLists.filter { !initialPlaylistIdsWithTrack.contains(it.id) }

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
                val topPlaylists = playLists.filter { initialPlaylistIdsWithTrack.contains(it.id) }

                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                    items(topPlaylists) { playList ->
                        PlayListItem(
                            playList = playList,
                            onAddInPlayListClick = {
                                playList.id?.let {
                                    onAddInPlayListClick(it)
                                }
                            },
                            onRemoveFromPlayList = {
                                playList.id?.let {
                                    onRemoveFromPlayList(it)
                                }
                            },
                            isInPlayList = playList.tracks?.contains(currentTrackToSave.id) == true,
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
                items(bottomPlaylists) { playList ->
                    PlayListItem(
                        playList = playList,
                        onAddInPlayListClick = {
                            playList.id?.let {
                                onAddInPlayListClick(
                                    it
                                )
                            }
                        },
                        onRemoveFromPlayList = {
                            playList.id?.let {
                                onRemoveFromPlayList(
                                    it
                                )
                            }
                        },
                        isInPlayList = playList.tracks?.contains(currentTrackToSave.id) == true,
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
    onAddInPlayListClick: () -> Unit,
    onRemoveFromPlayList: () -> Unit,
    isInPlayList: Boolean,
) {

    val songsCount = playList.tracks?.size?.toSongsString()

    var isAdded by remember { mutableStateOf(isInPlayList) }

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
        Icon(
            painter = painterResource(
                if (isAdded) R.drawable.ic_tick_circle
                else R.drawable.ic_circle_plus
            ),
            contentDescription = null,
            tint = if (isAdded) Color.Unspecified else Gray,
            modifier = Modifier
                .size(25.dp)
        )
    }

}

fun Int.toSongsString(): String {
    return if (this == 1) "1 song" else "$this songs"
}