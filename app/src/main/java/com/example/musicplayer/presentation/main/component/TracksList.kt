package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Track


@Composable
fun TracksList(
    tracks: List<Track>,
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onTrackClick = {
                    //старт
                }
            )
        }
    }

}