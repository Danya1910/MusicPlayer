package com.example.musicplayer.presentation.main.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.AddTrackToPlayListWidget
import com.example.musicplayer.presentation.main.component.AlbumView
import com.example.musicplayer.presentation.main.component.BottomBar
import com.example.musicplayer.presentation.main.component.MainScreenColumn
import com.example.musicplayer.presentation.main.component.ModalRightSheet
import com.example.musicplayer.presentation.main.component.NowPlayingBar
import com.example.musicplayer.presentation.main.component.PlayListNameInput
import com.example.musicplayer.presentation.main.component.TrackInfo
import com.example.musicplayer.presentation.main.component.TrackView
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
) {

    BackHandler(enabled = true) {}

    val hazeState = remember { HazeState() }

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    val currentTrack by mainViewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by mainViewModel.isPlaying.collectAsStateWithLifecycle()

    val userPlaylists by mainViewModel.playlistsState.collectAsStateWithLifecycle()

    var isAlbumViewVisible by remember { mutableStateOf(false) }
    var isTrackViewVisible by remember { mutableStateOf(false) }
    var isTrackInfoVisible by remember { mutableStateOf(false) }
    var isPlayListSelectionVisible by remember { mutableStateOf(false) }

    var clickedAlbum by remember { mutableStateOf<Album?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val playerState by mainViewModel.playerState.collectAsStateWithLifecycle()

    val upcomingTracks by mainViewModel.upcomingTracks.collectAsStateWithLifecycle()
    val currentQueue by mainViewModel.currentQueue.collectAsStateWithLifecycle()

    val trackInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val playListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val nameInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentInfoToShow by remember { mutableStateOf<Track?>(null) }
    var currentTrackForAdd by remember { mutableStateOf<Track?>(null) }
    var showPlayListName by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .haze(hazeState),
            bottomBar = {
                BottomBar(
                    navController = navController
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                when (val state = uiState) {
                    is MainUiState.Loading ->
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Загрузка нах...",
                            fontSize = 23.sp,
                            color = White,
                            modifier = Modifier.fillMaxSize()
                        )

                    is MainUiState.Success ->
                        if (state.tracks.isEmpty() && state.albums.isEmpty()) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Списки пустые. В базе данных ничего нет!",
                                fontSize = 18.sp,
                                color = White,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                            ) {
                                MainScreenColumn(
                                    tracks = state.tracks,
                                    albums = state.albums,
                                    onTrackClick = { track ->
                                        mainViewModel.playTrackWithQueue(
                                            track = track,
                                            queue = state.tracks,
                                        )
                                    },
                                    onAlbumClick = { album ->
                                        clickedAlbum = album
                                        isAlbumViewVisible = true
                                    },
                                    playerState = playerState,
                                    onMoreAboutTrack = { track ->
                                        currentInfoToShow = track
                                        isTrackInfoVisible = true
                                    },
                                )

                            }
                        }

                    is MainUiState.Error ->
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Ошибочка: ${state.message}",
                            fontSize = 23.sp,
                            color = White,
                            modifier = Modifier.fillMaxSize()
                        )
                }
                if (isAlbumViewVisible) {
                    ModalRightSheet(
                        onDismissRequest = {
                            isAlbumViewVisible = false
                            clickedAlbum = null
                        },
                        content = {
                            clickedAlbum?.let { album ->
                                AlbumView(
                                    album = album,
                                    onHeartClick = {

                                    },
                                    onCancelClick = {
                                        isAlbumViewVisible = false
                                        clickedAlbum = null
                                    },
                                    onPlayPauseClick = { tracks ->
                                        tracks.firstOrNull()?.let { firstTrack ->
                                            mainViewModel.playTrackWithQueue(
                                                track = firstTrack,
                                                queue = tracks,
                                            )
                                        }
                                    },
                                    onDownloadAlbumClick = {

                                    },
                                    onBitmapLoaded = { bitmap ->
                                        mainViewModel.updateColorsFromBitmap(
                                            id = album.id,
                                            bitmap = bitmap,
                                        )
                                    },
                                    playerState = playerState,
                                )
                            }
                        }
                    )
                }

                currentTrack?.let { track ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 5.dp)
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
                    playLists = emptyList(),
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
                        scope.launch {
                            mainViewModel.addTrackToPlayList(
                                playListId = id,
                                trackId = currentTrackForAdd!!.id
                            )
                        }
                    }
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
                            trackId = track.id
                        )
                        showPlayListName = false
                    },
                    trackName = track.title
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

}