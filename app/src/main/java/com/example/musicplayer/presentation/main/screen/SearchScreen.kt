package com.example.musicplayer.presentation.main.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.main.SearchViewModel
import com.example.musicplayer.presentation.main.component.SearchGenreItem
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
) {

    val hazeState = remember { HazeState() }

    var query by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val focusManager = LocalFocusManager.current

    val searchFieldHeight = 166.dp

    val offsetY by animateDpAsState(
        targetValue = if (isFocused) (-60).dp else 0.dp,
        animationSpec = tween(250),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = !isFocused,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(250),
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(200),
            ),
        ) {
            TopBar()
        }


        Box(
            modifier = Modifier
                .offset(
                    y = offsetY
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = searchFieldHeight + 12.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .haze(
                        state = hazeState
                    )
                    .padding(
                        horizontal = 12.dp
                    )
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SearchGenreItem(
                            title = "Pop",
                            color = Color(0xFF477D95),
                            icon = R.drawable.pop,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SearchGenreItem(
                            title = "Hip-Hop",
                            color = Color(0xFFE13300),
                            icon = R.drawable.hip_hop,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SearchGenreItem(
                            title = "Emo Rap",
                            color = Color(0xFF51095E),
                            icon = R.drawable.emo_rap,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SearchGenreItem(
                            title = "Hip-Hop",
                            color = Color(0xFF477D95),
                            icon = R.drawable.hip_hop,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(
                        top = 60.dp
                    )
            ) {
                SearchField(
                    value = query,
                    onQueryChange = {
                        query = it
                        searchViewModel.onQueryChanged(
                            query = it
                        )
                    },
                    onCancelClick = {
                        query = ""
                        focusManager.clearFocus()
                    },
                    isFocused = isFocused,
                    interactionSource = interactionSource,
                    hintText = "What do you want to listen to?",
                    hazeState = hazeState,
                )
            }
        }
    }
}

@Composable
private fun TopBar() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 10.dp)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_user_avatar),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Search",
            fontWeight = FontWeight.SemiBold,
            fontSize = 27.sp,
            color = White,
        )
    }

}

@Composable
private fun SearchField(
    value: String,
    onQueryChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    isFocused: Boolean,
    interactionSource: MutableInteractionSource,
    hintText: String,
    hazeState: HazeState,
) {

    val showCancel = isFocused

    val gradientColors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
        Color(0xFFFFFFFF).copy(alpha = 0.3f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
    )


    val cornerRadius by animateDpAsState(
        targetValue = if (isFocused) 100.dp else 5.dp,
        animationSpec = tween(durationMillis = 250),
    )

    val fieldPadding by animateDpAsState(
        targetValue = if (showCancel) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
    )

    val fieldColor by animateColorAsState(
        targetValue = if (isFocused) DeepBlack else White,
        animationSpec = tween(durationMillis = 250),
    )

    val textColor by animateColorAsState(
        targetValue = if (isFocused) White else Black,
        animationSpec = tween(durationMillis = 250),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .weight(1f)
                .clip(
                    shape = RoundedCornerShape(cornerRadius)
                )
                .hazeChild(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = fieldColor,
                        blurRadius = 10.dp,
                        tint = HazeTint(color = DeepBlack.copy(alpha = 0.0f)),
                        noiseFactor = 0.001f
                    ),
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
                    end = fieldPadding
                )
        ) {
            BasicTextField(
                value = value,
                onValueChange = onQueryChange,
                interactionSource = interactionSource,
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor,
                ),
                decorationBox = { innerTextField ->

                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 12.dp)
                    ) {
                        if (value.isEmpty() && hintText.isNotEmpty()) {
                            Text(
                                hintText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        AnimatedVisibility(
            visible = showCancel,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(250),
            ) + fadeIn(tween(250)),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(200),
            ) + fadeOut(tween(200)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                CloseButton(
                    text = "Cancel",
                    onClick = {
                        onCancelClick()
                    },
                    hazeState = hazeState,
                )
            }
        }
    }

}

@Composable
private fun CloseButton(
    text: String,
    onClick: () -> Unit,
    hazeState: HazeState,
) {

    val gradientColors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
        Color(0xFFFFFFFF).copy(alpha = 0.3f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFFFFFFF).copy(alpha = 0.2f),
        Color(0xFFFFFFFF).copy(alpha = 0.5f),
    )

    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(
                shape = CircleShape
            )
            .hazeChild(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = DeepBlack,
                    blurRadius = 10.dp,
                    tint = HazeTint(color = DeepBlack.copy(alpha = 0.8f)),
                    noiseFactor = 0.001f
                ),
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
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = White,
        )
    }

}


