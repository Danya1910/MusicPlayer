package com.example.musicplayer.presentation.main.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.User
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.BottomBar
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White


@Composable
fun LibraryScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {

    val playlists by mainViewModel.playlistsState.collectAsStateWithLifecycle()

    val user by mainViewModel.userState.collectAsStateWithLifecycle()

    Log.d("Library", user.toString())

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                user = user,
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = DeepBlack
                )
                .padding(paddingValues)
        ) {

            items(playlists) {playlist ->
                PlayListItem(
                    playList = playlist,
                    onClick = {

                    }
                )
            }
        }
    }

}

@Composable
private fun PlayListItem(
    playList: PlayList,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable{
                onClick()
            }
            .padding(horizontal = 20.dp)
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
            verticalArrangement = Arrangement.Center
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Playlist · ${playList.creatorName}",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Gray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
        }
    }
}


@Composable
private fun TopBar(
    user: User,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Your Library",
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
                    .basicMarquee()
            )
            Icon(
                painter = painterResource(R.drawable.ic_search_for_field),
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            TopBarItem(
                title = "Playlists",
                onClick = {}
            )
            TopBarItem(
                title = "Albums",
                onClick = {}
            )
            TopBarItem(
                title = "Artists",
                onClick = {}
            )
        }
    }
}

@Composable
private fun TopBarItem(
    title: String,
    onClick: () -> Unit,
) {

    val gradientColors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.8f),
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
        Color(0xFFFFFFFF).copy(alpha = 0.3f),
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFFFFFFF).copy(alpha = 0.3f),
        Color(0xFFFFFFFF).copy(alpha = 0.8f),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(
                shape = CircleShape
            )
            .background(
                color = DeepBlack,
            )
            .border(
                width = 1.dp,
                shape = CircleShape,
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
            .clickable{
                onClick()
            }
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = White,
        )
    }
}