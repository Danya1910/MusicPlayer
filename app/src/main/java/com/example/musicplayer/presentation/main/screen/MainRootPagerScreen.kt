package com.example.musicplayer.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.AddTrackToPlayListWidget
import com.example.musicplayer.presentation.main.component.BottomBarTest
import com.example.musicplayer.presentation.main.component.NowPlayingBar
import com.example.musicplayer.presentation.main.component.PlayListNameInput
import com.example.musicplayer.presentation.main.component.TrackInfo
import com.example.musicplayer.presentation.main.component.TrackView
import com.example.musicplayer.presentation.navigation.TabScreen
import com.example.musicplayer.ui.theme.DeepBlack
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRootPagerScreen(
    mainViewModel: MainViewModel,
    rootNavController: NavController
) {

    val hazeState = remember { HazeState() }

    val playerState by mainViewModel.playerState.collectAsStateWithLifecycle()
    val userPlaylists by mainViewModel.playlistsState.collectAsStateWithLifecycle()

    val currentTrack = playerState.currentTrack

    val isPlaying = playerState.isPlaying

    val scope = rememberCoroutineScope()

    val upcomingTracks by mainViewModel.upcomingTracks.collectAsStateWithLifecycle()
    val currentQueue by mainViewModel.currentQueue.collectAsStateWithLifecycle()

    val tabNavController = rememberNavController()

    val backStackEntry by tabNavController.currentBackStackEntryAsState()
    val selectedTab: TabScreen = when (backStackEntry?.destination?.route) {
        TabScreen.Home::class.qualifiedName -> TabScreen.Home
        TabScreen.Search::class.qualifiedName -> TabScreen.Search
        TabScreen.Library::class.qualifiedName -> TabScreen.Library
        else -> TabScreen.Home
    }

    val bottomPadding = if (currentTrack != null) 145.dp else 85.dp

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val trackInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val playListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val nameInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var isTrackViewVisible by remember { mutableStateOf(false) }
    var isTrackInfoVisible by remember { mutableStateOf(false) }
    var isPlayListSelectionVisible by remember { mutableStateOf(false) }

    var currentInfoToShow by remember { mutableStateOf<Track?>(null) }
    var currentTrackForAdd by remember { mutableStateOf<Track?>(null) }
    var showPlayListName by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack
            )
            .haze(hazeState)
    ) {

        NavHost(
            navController = tabNavController,
            startDestination = TabScreen.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding)
        ) {

            composable<TabScreen.Home> {
                MainTest(
                    mainViewModel = mainViewModel,
                    navController = rootNavController,
                    onMoreClick = { track ->
                        currentInfoToShow = track
                        isTrackInfoVisible = true
                    }
                )
            }

            composable<TabScreen.Search> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepBlack)
                )
            }

            composable<TabScreen.Library> {
                LibraryTest(
                    mainViewModel = mainViewModel,
                    navController = rootNavController,
                )
            }

        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomBarTest(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    tabNavController.navigate(tab) {
                        popUpTo(tabNavController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        currentTrack?.let { track ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 85.dp)
                    .navigationBarsPadding()
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

        if (isTrackInfoVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val offset =
                            runCatching { trackInfoSheetState.requireOffset() }.getOrNull() ?: 0f

                        val startFadeOffset = 500f

                        val alphaProgress = if (offset > startFadeOffset) {
                            (0.8f - ((offset - startFadeOffset) / 1800f)).coerceIn(0f, 0.8f)
                        } else {
                            0.8f
                        }
                        alpha = alphaProgress
                    }
                    .background(DeepBlack)
            )
        }

        if (isTrackInfoVisible) {

            ModalBottomSheet(
                onDismissRequest = { isTrackInfoVisible = false },
                sheetState = trackInfoSheetState,
                containerColor = Color.Transparent,
                dragHandle = null, // добавить
                scrimColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                currentInfoToShow?.let {
                    TrackInfo(
                        track = currentInfoToShow!!,
                        hazeState = hazeState,
                        playNext = {
                            mainViewModel.playNext(
                                track = currentInfoToShow!!
                            )
                        },
                        addToPlayList = {
                            currentTrackForAdd = currentInfoToShow!!
                            isTrackInfoVisible = false
                            isPlayListSelectionVisible = true
                        }
                    )
                }
            }
        }

        if (isTrackViewVisible) {
            ModalBottomSheet(
                onDismissRequest = { isTrackViewVisible = false },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                scrimColor = DeepBlack.copy(alpha = 0.5f),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                modifier = Modifier.fillMaxSize()
            ) {
                currentTrack?.let { track ->
                    TrackView(
                        track = track,
                        onCancelClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    isTrackViewVisible = false
                                }
                            }
                        },
                        onPauseClick = {
                            mainViewModel.togglePlayPause()
                        },
                        onHeartClick = {
                           mainViewModel.toggleFavorite(
                               track = track,
                               )

                        },
                        onLoadClick = {
                            mainViewModel.toggleFile(
                                track = track,
                            )
                        },
                        playerState = playerState,
                        onBitmapLoaded = { bitmap ->
                            mainViewModel.updateColorsFromBitmap(
                                id = track.id,
                                bitmap = bitmap,
                            )
                        },
                        isPlaying = isPlaying,
                        onSkipToNext = {
                            mainViewModel.skipToNext()
                        },
                        onSkipToPrevious = {
                            mainViewModel.skipToPrevious()
                        },
                        onSeekTo = { ms ->
                            mainViewModel.seekTo(positionMs = ms)
                        },
                        onShuffleClick = {
                            mainViewModel.toggleShuffleMode()
                        },
                        onRepeatClick = {
                            mainViewModel.toggleRepeatMode()
                        },
                        isShuffled = playerState.isShuffleEnabled,
                        repeatMode = playerState.repeatMode,
                        upcomingTracks = upcomingTracks,
                        currentQueue = currentQueue,
                        onTrackInQueueClick = { track ->
                            mainViewModel.playTrackWithQueue(
                                track = track,
                                queue = currentQueue
                            )
                        },
                        onMoveTrack = { fromIndex, toIndex ->
                            mainViewModel.moveTrack(
                                fromIndex = fromIndex,
                                toIndex = toIndex,
                            )
                        },
                        removeFromQueue = { index ->
                            mainViewModel.removeTrackFromQueue(
                                index = index
                            )
                        }
                    )

                }
            }
        }

        if (isPlayListSelectionVisible) {
            ModalBottomSheet(
                onDismissRequest = { isPlayListSelectionVisible = false },
                sheetState = playListSheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                scrimColor = DeepBlack.copy(alpha = 0.5f),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                modifier = Modifier.fillMaxSize()
            ) {
                currentTrackForAdd?.let {
                    AddTrackToPlayListWidget(
                        playLists = userPlaylists,
                        onCancelClick = {
                            isPlayListSelectionVisible = false
                            currentTrackForAdd = null
                        },
                        onDoneClick = {
                            isPlayListSelectionVisible = false
                            currentTrackForAdd = null
                        },
                        onCreateClick = {
                            showPlayListName = true
                            isPlayListSelectionVisible = false
                        },
                        onAddInPlayListClick = { id ->
                            mainViewModel.addTrackToPlayList(
                                playListId = id,
                                trackId = currentTrackForAdd!!.id
                            )
                        },
                        onRemoveFromPlayList = { id ->
                            mainViewModel.removeTrackFromPlayList(
                                playListId = id,
                                trackId = currentTrackForAdd!!.id
                            )
                        },
                        currentTrackToSave = currentTrackForAdd!!,
                    )
                }
            }
        }

        if (showPlayListName) {
            ModalBottomSheet(
                onDismissRequest = { showPlayListName = false },
                sheetState = nameInputSheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                scrimColor = DeepBlack.copy(alpha = 0.5f),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                modifier = Modifier.fillMaxSize()
            ) {
                currentTrackForAdd?.let { track ->
                    PlayListNameInput(
                        onCreateClick = { title ->
                            mainViewModel.createPlayListWithTrack(
                                title = title,
                                trackId = track.id,
                                imageUrl = track.imageUrl,
                            )
                            showPlayListName = false
                        },
                        trackName = track.title
                    )
                }
            }
        }

    }

}