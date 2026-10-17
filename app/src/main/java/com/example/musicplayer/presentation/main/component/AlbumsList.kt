package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Album


@Composable
fun AlbumsList(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit,
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { album ->
            AlbumItem(
                album = album,
                onClick = {
                    onAlbumClick(album.id)
                }
            )
        }
    }

}