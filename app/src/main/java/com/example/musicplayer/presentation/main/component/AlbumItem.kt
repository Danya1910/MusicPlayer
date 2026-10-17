package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.ui.theme.White


@Composable
fun Album(
    album: Album,
    onClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .width(154.dp)
            .clickable{
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .size(154.dp)
        ) {
            AsyncImage(
                model = album.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = album.title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = White,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = album.artist,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = Color(0xFFB3B3B3),
        )
    }

}