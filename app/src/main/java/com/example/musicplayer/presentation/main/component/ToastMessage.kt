package com.example.musicplayer.presentation.main.component

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.main.util.ToastData
import com.example.musicplayer.presentation.main.util.ToastType
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import kotlinx.coroutines.delay


@Composable
fun ToastHost(
    toast: ToastData?,
    onDismiss: () -> Unit,
    onChangeClick: () -> Unit,
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
                onChangeClick = onChangeClick
            )
        }
    }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2500)
            onDismiss()
        }
    }

}

@Composable
private fun ToastContent(
    toastData: ToastData,
    onChangeClick: () -> Unit,
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
            .padding(horizontal = 13.dp)
    ) {
        Icon(
            painter = painterResource(
                if (toastData.type == ToastType.ERROR) R.drawable.ic_error_favorites
                else R.drawable.ic_liked_songs
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(36.dp)
                .clip(
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = toastData.message ?: "",
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = Black,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Change",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = SpotifyGreen,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onChangeClick()
                }
        )
    }

}