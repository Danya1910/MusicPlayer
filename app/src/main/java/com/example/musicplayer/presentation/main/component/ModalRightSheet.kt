package com.example.musicplayer.presentation.main.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class DragValue { Open, Closed }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModalRightSheet(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val scope = rememberCoroutineScope()


    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val snapAnimationSpec: AnimationSpec<Float> = tween(durationMillis = 300)

    val anchors = remember(screenWidthPx) {
        DraggableAnchors {
            DragValue.Open at 0f
            DragValue.Closed at screenWidthPx
        }
    }

    val draggableState = remember(anchors) {
        AnchoredDraggableState(
            initialValue = DragValue.Closed,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.3f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec
        )
    }

    val isSheetOpen = draggableState.offset < screenWidthPx

    BackHandler(enabled = isSheetOpen) {
        scope.launch {
            draggableState.animateTo(DragValue.Closed)
        }
    }

    LaunchedEffect(draggableState) {
        draggableState.animateTo(DragValue.Open)
    }

    LaunchedEffect(draggableState.currentValue) {
        if (draggableState.currentValue == DragValue.Closed && !draggableState.isAnimationRunning) {
            onDismissRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val offsetValue = if (draggableState.offset.isNaN()) screenWidthPx else draggableState.offset
        val alpha = (1f - (offsetValue / screenWidthPx)).coerceIn(0f, 0.5f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismissRequest()
                }
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(configuration.screenWidthDp.dp)
                .offset { IntOffset(x = offsetValue.roundToInt(), y = 0) }
                .background(Color(0xFF121212))
        ) {
            content()

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .padding(top = 44.dp)
                    .width(30.dp)
                    .systemGestureExclusion()
                    .anchoredDraggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal
                    )
            )
        }
    }
}
