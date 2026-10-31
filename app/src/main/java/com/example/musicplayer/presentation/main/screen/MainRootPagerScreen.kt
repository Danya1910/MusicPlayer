package com.example.musicplayer.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.BottomBar
import com.example.musicplayer.presentation.main.component.BottomBarTest
import com.example.musicplayer.presentation.main.component.NowPlayingBar
import com.example.musicplayer.ui.theme.DeepBlack
import kotlinx.coroutines.launch


@Composable
fun MainRootPagerScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {

    val pagerState = rememberPagerState(pageCount = { 3 })

    val playerState by mainViewModel.playerState.collectAsStateWithLifecycle()
    val user by mainViewModel.userState.collectAsStateWithLifecycle()
    val currentTrack = playerState.currentTrack

    val isPlaying = playerState.isPlaying

    var isTrackViewVisible by remember { mutableStateOf(false) }

    val bottomPadding = if (currentTrack != null) {
        60.dp
    } else 0.dp


    var topPadding = if (pagerState.currentPage == 2) {
        130.dp
    } else 0.dp

    val scope = rememberCoroutineScope()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = DeepBlack
                )
                .padding(
                    top = topPadding,
                    bottom = bottomPadding + 85.dp
                )
        ) { page ->
            when (page) {
                0 -> MainTest(
                    mainViewModel = mainViewModel,
                    navController = navController,
                )

                1 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Blue)
                )

                2 -> LibraryTest(
                    mainViewModel = mainViewModel,
                    navController = navController,
                )
            }
        }


        currentTrack?.let { track ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 85.dp)
            ) {
                NowPlayingBar(
                    track = track,
                    onPlayPauseClick = {
                        mainViewModel.togglePlayPause()
                    },
                    onCardClick = {
                        isTrackViewVisible = true
                    },
                    isPlaying = isPlaying,
                    currentPosition = playerState.currentPosition,
                    duration = track.duration,
                )
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(85.dp)
                .background(Color.Red)
                .zIndex(10f)
        )
    }

}