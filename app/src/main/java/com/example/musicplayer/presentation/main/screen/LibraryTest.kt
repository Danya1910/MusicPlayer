package com.example.musicplayer.presentation.main.screen

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.PlayList
import com.example.musicplayer.domain.model.PlaybackOrigin
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.model.User
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.component.AlbumView
import com.example.musicplayer.presentation.main.component.LikedView
import com.example.musicplayer.presentation.main.component.ModalRightSheet
import com.example.musicplayer.presentation.main.component.PlaylistView
import com.example.musicplayer.presentation.main.state.MainUiState
import com.example.musicplayer.presentation.main.util.LibraryTab
import com.example.musicplayer.presentation.main.util.PlaylistUi
import com.example.musicplayer.presentation.navigation.TabScreen
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White


@Composable
fun LibraryTest(
    mainViewModel: MainViewModel,
    navController: NavController,
    tabController: NavController,
    favorites: PlaylistUi,
    onMoreClick: (Track) -> Unit,
) {

    val playlists by mainViewModel.playlistsUiState.collectAsStateWithLifecycle()

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    val downloads by mainViewModel.downloads.collectAsStateWithLifecycle()

    val playbackOrigin by mainViewModel.currentPlayingOrigin.collectAsStateWithLifecycle()

    val playerState by mainViewModel.playerState.collectAsStateWithLifecycle()

    val user by mainViewModel.userState.collectAsStateWithLifecycle()

    var currentPlaylistToShow by remember { mutableStateOf<PlaylistUi?>(null) }
    var showPlayList by remember { mutableStateOf(false) }

    var currentAlbumToShow by remember { mutableStateOf<Album?>(null) }
    var showAlbum by remember { mutableStateOf(false) }

    val playlistToPass = if (currentPlaylistToShow != null) {
        if (currentPlaylistToShow!!.playlist.id == favorites.playlist.id) favorites
        else playlists.find { it.playlist.id == currentPlaylistToShow!!.playlist.id }
    } else null

    val albumToPass = currentAlbumToShow?.let { current ->
        (uiState as? MainUiState.Success)
            ?.albums
            ?.find { it.id == current.id }
    }


    var selectedTab by remember { mutableStateOf(LibraryTab.All) }

    LaunchedEffect(selectedTab) {
        Log.d("Library", "selectedTab: $selectedTab")
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LibraryTopBar(
            user = user,
            changeTab = { tab ->
                if (selectedTab == tab)
                    selectedTab = LibraryTab.All
                else
                    selectedTab = tab
            },
            selectedTab = selectedTab,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = DeepBlack
                )
        ) {

            if (selectedTab == LibraryTab.Playlists || selectedTab == LibraryTab.All) {
                item {
                    FavoritesItem(
                        playList = favorites.playlist,
                        onClick = {
                            currentPlaylistToShow = favorites
                            showPlayList = true
                        }
                    )
                }

                items(playlists) { playlist ->
                    PlayListItem(
                        playList = playlist.playlist,
                        onClick = {
                            currentPlaylistToShow = playlist
                            showPlayList = true
                        }
                    )
                }
            }

            if (selectedTab == LibraryTab.Albums || selectedTab == LibraryTab.All)
                when (val state = uiState) {
                    is MainUiState.Success -> {
                        val favorites = state.albums.filter { it.isFavorite }
                        if (favorites.isEmpty()) {
                        } else {
                            items(favorites, key = { it.id }) { album ->
                                AlbumItem(
                                    album = album,
                                    onClick = {
                                        currentAlbumToShow = album
                                        showAlbum = true
                                    }
                                )
                            }
                        }
                    }

                    MainUiState.Loading -> {
                        // скелетон / спиннер
                    }

                    is MainUiState.Error -> {
                        // текст ошибки
                    }
                }
        }
    }

    if (showAlbum) {
        ModalRightSheet(
            onDismissRequest = {
                showAlbum = false
                currentAlbumToShow = null
            },
            content = {
                albumToPass?.let { album ->
                    AlbumView(
                        album = album,
                        onHeartClick = {
                            mainViewModel.toggleAlbumLike(
                                album = album,
                            )
                        },
                        onCancelClick = {
                            showAlbum = false
                            currentAlbumToShow = null
                        },
                        onPlayClick = { tracks ->
                            tracks.firstOrNull()?.let { firstTrack ->
                                mainViewModel.playTrackWithQueue(
                                    track = firstTrack,
                                    queue = tracks,
                                    playbackOrigin = PlaybackOrigin.Album(
                                        albumId = album.id,
                                    ),
                                )
                            }
                        },
                        onContinuePlayClick = {
                            mainViewModel.togglePlayPause()
                        },
                        onPauseClick = {
                            mainViewModel.togglePlayPause()
                        },
                        onDownloadAlbumClick = {
                            mainViewModel.toggleListOfTracks(
                                tracks = album.tracks,
                            )
                        },
                        onBitmapLoaded = { bitmap ->
                            mainViewModel.updateColorsFromBitmap(
                                id = album.id,
                                bitmap = bitmap,
                            )
                        },
                        playerState = playerState,
                        onMoreClick = { track ->
                            onMoreClick(track)
                        },
                        onTrackClick = { track ->
                            mainViewModel.playTrackWithQueue(
                                track = track,
                                queue = albumToPass.tracks,
                                playbackOrigin = PlaybackOrigin.Album(
                                    albumId = album.id,
                                ),
                            )
                        },
                        toggleFavorite = { track ->
                            mainViewModel.toggleFavorite(track)
                        },
                        onDownloadClick = { track ->
                            mainViewModel.onDownloadClick(track)
                        },
                        downloads = downloads,
                        currentPlayingOrigin = playbackOrigin,
                    )
                }
            }
        )
    }

    if (showPlayList) {
        ModalRightSheet(
            onDismissRequest = {
                showPlayList = false
                currentPlaylistToShow = null
            },
            content = {
                playlistToPass?.let { playList ->
                    if (playList.playlist.id == "favorites") {
                        LikedView(
                            playList = playList,
                            tracks = playList.tracks,
                            playerState = playerState,
                            onPlayClick = { tracks ->
                                tracks.firstOrNull()?.let { firstTrack ->
                                    mainViewModel.playTrackWithQueue(
                                        track = firstTrack,
                                        queue = tracks,
                                        playbackOrigin = PlaybackOrigin.Playlist(
                                            playlistId = playList.playlist.id ?: "",
                                        ),
                                    )
                                }
                            },
                            onContinuePlayClick = {
                                mainViewModel.togglePlayPause()
                            },
                            onPauseClick = {
                                mainViewModel.togglePlayPause()
                            },
                            onShuffleClick = {
                                mainViewModel.toggleShuffleMode()
                            },
                            onTrackClick = { track ->
                                mainViewModel.playTrackWithQueue(
                                    track = track,
                                    queue = playList.tracks,
                                    playbackOrigin = PlaybackOrigin.Playlist(
                                        playlistId = playList.playlist.id,
                                    )
                                )
                            },
                            onTrackInfoClick = {},
                            onDownloadClick = { track ->
                                mainViewModel.onDownloadClick(track)
                            },
                            onBackClick = {
                                showPlayList = false
                                currentPlaylistToShow = null
                            },
                            toggleFavorite = { track ->
                                mainViewModel.toggleFavorite(track)
                            },
                            onMoreClick = { track ->
                                onMoreClick(track)
                            },
                            downloads = downloads,
                            navigateToHome = {
                                tabController.navigate(TabScreen.Home)
                            },
                            toggleLoadPlaylist = {
                                mainViewModel.toggleListOfTracks(
                                    tracks = playList.tracks,
                                )
                            },
                            currentPlayingOrigin = playbackOrigin,
                        )
                    } else {
                        PlaylistView(
                            playList = playList,
                            tracks = playList.tracks,
                            playerState = playerState,
                            onPlayClick = { tracks ->
                                tracks.firstOrNull()?.let { firstTrack ->
                                    mainViewModel.playTrackWithQueue(
                                        track = firstTrack,
                                        queue = tracks,
                                        playbackOrigin = PlaybackOrigin.Playlist(
                                            playlistId = playList.playlist.id ?: "",
                                        ),
                                    )
                                }
                            },
                            onContinuePlayClick = {
                                mainViewModel.togglePlayPause()
                            },
                            onPauseClick = {
                                mainViewModel.togglePlayPause()
                            },
                            onShuffleClick = {
                                mainViewModel.toggleShuffleMode()
                            },
                            onTrackClick = { track ->
                                mainViewModel.playTrackWithQueue(
                                    track = track,
                                    queue = playList.tracks,
                                    playbackOrigin = PlaybackOrigin.Playlist(
                                        playlistId = playList.playlist.id ?: "",
                                    )
                                )
                            },
                            onTrackInfoClick = {},
                            onDownloadClick = { track ->
                                mainViewModel.onDownloadClick(track)
                            },
                            onBackClick = {
                                showPlayList = false
                                currentPlaylistToShow = null
                            },
                            onBitmapLoaded = { bitmap ->
                                mainViewModel.updateColorsFromBitmap(
                                    id = playList.playlist.id ?: "",
                                    bitmap = bitmap,
                                )
                            },
                            toggleFavorite = { track ->
                                mainViewModel.toggleFavorite(
                                    track = track,
                                )
                            },
                            onMoreClick = { track ->
                                onMoreClick(track)
                            },
                            downloads = downloads,
                            toggleLoadPlaylist = {
                                mainViewModel.toggleListOfTracks(
                                    tracks = playList.tracks,
                                )
                            },
                            currentPlayingOrigin = playbackOrigin,
                        )
                    }
                }
            }
        )
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
            .clickable {
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
private fun FavoritesItem(
    playList: PlayList,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_liked_songs),
            contentDescription = null,
            tint = Color.Unspecified,
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
                text = "Liked Songs",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text =
                    if (playList.tracks?.size == 0) "Empty"
                    else
                        songsFormat(
                            songsCount = playList.tracks?.size ?: 0
                        ),
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
private fun AlbumItem(
    album: Album,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 20.dp)
    ) {
        AsyncImage(
            model = album.imageUrl,
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
                text = album.title,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Album · ${album.artist}",
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

private fun songsFormat(
    songsCount: Int,
): String {
    return if (songsCount == 1)
        "1 song"
    else "$songsCount songs"
}


@Composable
private fun LibraryTopBar(
    user: User,
    changeTab: (LibraryTab) -> Unit,
    selectedTab: LibraryTab,
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
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            TopBarItem(
                title = "Playlists",
                onClick = {
                    changeTab(LibraryTab.Playlists)
                },
                selected = selectedTab == LibraryTab.Playlists,
            )
            TopBarItem(
                title = "Albums",
                onClick = {
                    changeTab(LibraryTab.Albums)
                },
                selected = selectedTab == LibraryTab.Albums,
            )
            TopBarItem(
                title = "Artists",
                onClick = {
                },
                selected = selectedTab == LibraryTab.Artists,
            )
        }
    }
}

@Composable
private fun TopBarItem(
    title: String,
    onClick: () -> Unit,
    selected: Boolean,
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) DarkGray else DeepBlack,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "TopBarItem_$title"
    )

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
                color = backgroundColor,
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
            .clickable {
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