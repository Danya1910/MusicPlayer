package com.example.musicplayer.presentation.auth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.ui.theme.White

@Composable
fun LogInButton(
    onClick: () -> Unit,
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(49.dp)
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(45.dp)
            )
            .clickable{
                onClick()
            }
    ) {
        Text(
            text = "Log In",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = White,
        )
    }

}