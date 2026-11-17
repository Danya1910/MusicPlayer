package com.example.musicplayer.presentation.auth.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.presentation.auth.util.AuthToastData
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun AuthToastHost(
    toast: AuthToastData?,
    onDismiss: () -> Unit,
    modifier: Modifier,
) {

    AnimatedVisibility(
        visible = toast != null,
        enter = scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(250)
        ),
        exit = scaleOut(
            targetScale = 0.85f,
            animationSpec = tween(200)
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        toast?.let { data ->
            ToastContent(
                toastData = data,
            )
        }
    }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(3000)
            onDismiss()
        }
    }

}

@Composable
private fun ToastContent(
    toastData: AuthToastData,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = White,
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(40.dp)
            )
            .padding(horizontal = 13.dp)
    ) {

        Text(
            textAlign = TextAlign.Center,
            text = toastData.message ?: "",
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = Black,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
        )

    }

}