package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.presentation.main.util.shimmer
import com.example.musicplayer.ui.theme.White

@Composable
fun MainScreenColumnSkeleton() {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {

        item {
            Text(
                text = "Слушать плейлисты",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            AlbumsListSkeleton()
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Text(
                text = "Слушать музыку",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        items(
            count = 10
        ) {
            TrackItemSkeleton()
        }
    }
}

@Composable
fun AlbumsListSkeleton(
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp)
    ) {
        items(3) {
            AlbumItemSkeleton()
        }
    }

}

@Composable
fun AlbumItemSkeleton() {

    Column(
        modifier = Modifier
            .width(154.dp)
    ) {

        Box(
            modifier = Modifier
                .size(154.dp)
                .shimmer()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .shimmer()
        ) {
            Text(
                text = "Some text",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Transparent,
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .shimmer()
        ) {
            Text(
                text = "",
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = Color.Transparent,
            )
        }
    }

}

@Composable
fun TrackItemSkeleton(
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shimmer()
    ) {

    }

}