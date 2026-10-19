package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White


@Composable
fun TrackView(
    track: Track,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp
            )
            .background(
                color = DeepBlack,
            )
    ) {
        PlayerHead(
            onCancelClick = {},
            title = track.title,
        )

        Spacer(modifier = Modifier.weight(1f))

        AlbumImage(url = track.imageUrl)

        Spacer(modifier = Modifier.weight(1f))

        TrackInfoAndHeart(
            title = "Hope",
            artist = "XXXTENTACION",
            onHeartClick = {}
        )

        Spacer(modifier = Modifier.height(15.dp))

    }

}

@Composable
@Preview(showBackground = true)
private fun TopBarPreview() {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(
                DeepBlack,
            )
    ) {
        PlayerHead(
            onCancelClick = {},
            title = "Hope"
        )
        TrackInfoAndHeart(
            title = "Hope",
            artist = "XXXTENTACION",
            onHeartClick = {}
        )
    }
}

@Composable
private fun PlayerHead(
    onCancelClick: () -> Unit,
    title: String,
) {

    Row(
        modifier = Modifier
            .height(30.dp)
            .fillMaxWidth()
            .background(
                color = DeepBlack
            )
            .statusBarsPadding()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .size(21.dp)
                .clickable {
                    onCancelClick()
                }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            textAlign = TextAlign.Center,
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_ellipsis),
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(27.dp)
        )
    }

}

@Composable
private fun AlbumImage(
    url: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TrackInfoAndHeart(
    title: String,
    artist: String,
    onHeartClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 15.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = artist,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color(0xFFB3B3B3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(42.dp))
        Icon(
            painter = painterResource(R.drawable.ic_heart),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .size(23.dp)
                .clickable{
                    onHeartClick()
                },
        )
    }
}

@Composable
private fun PlayerSlider(

) {

    

}

