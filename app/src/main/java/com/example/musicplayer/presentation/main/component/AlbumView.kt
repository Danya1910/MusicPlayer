package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White


@Composable
fun AlbumView() {

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