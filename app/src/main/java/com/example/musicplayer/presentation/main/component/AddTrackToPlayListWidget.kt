package com.example.musicplayer.presentation.main.component

import android.widget.Space
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.presentation.main.util.glassEffect
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White

@Composable
fun AddTrackToPlayListWidget(
    playLists: List<PlayList>,
    onCancelClick: () -> Unit,
    onDoneClick: () -> Unit,
    onCreateClick: () -> Unit,
    onAddInPlayListClick: (String) -> Unit,
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
        ActionButtons(
            onCancelClick = onCancelClick,
            onDoneClick = onDoneClick,
        )
        Spacer(modifier = Modifier.height(25.dp))
        CreatePlayList(
            onCreateClick = onCreateClick,
        )
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Gray
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
    ) {
        Text(
            text = "Cancel",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
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
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
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
    ) {
        Text(
            text = "Save in",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = White,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "New playlist",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = White,
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
) {

    val songsCount = playList.tracks?.size?.toSongsString()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
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
            painter = painterResource(R.drawable.ic_circle_plus),
            contentDescription = null,
            tint = Gray,
            modifier = Modifier
                .size(25.dp)
                .padding(all = 5.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ){
                    onAddInPlayListClick()
                }
        )
    }

}

fun Int.toSongsString(): String {
    return if(this == 1) "1 song" else "$this songs"
}