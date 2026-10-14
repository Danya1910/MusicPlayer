package com.example.musicplayer.presentation.auth.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeScreen(

) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Icon()
    }

}

@Composable
@Preview(showBackground = true)
private fun WelcomeScreenPreview() {
    WelcomeScreen()
}