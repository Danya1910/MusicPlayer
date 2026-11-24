package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.White


@Composable
fun SearchGenreItem(
    title: String,
    color: Color,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier,
) {

    Box(
        modifier = modifier
            .height(115.dp)
            .clip(
                shape = RoundedCornerShape(5.dp)
            )
            .background(
                color = color,
            )
            .clickable{
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = 15.dp,
                    start = 12.dp,
                    end = 70.dp
                )
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = White,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }

}