package com.example.musicplayer.presentation.auth.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.auth.component.LogInButton
import com.example.musicplayer.presentation.auth.component.SocialMediaAccountButton
import com.example.musicplayer.presentation.auth.component.WelcomeButton
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White

@Composable
fun WelcomeScreen(

) {

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack,
            )
    ) {
        Image(
            painter = painterResource(R.drawable.welcome_bg),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 46.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                tint = White,
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Million of Songs.\nFree on Spotify.",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = White,
            )
            Spacer(modifier = Modifier.height(30.dp))
            WelcomeButton(
                text = "Sign up free",
                color = SpotifyGreen,
                onClick = {
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_google_logo,
                text = "Countinue with Google",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_facebook_logo,
                text = "Countinue with Facebook",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_apple_logo,
                text = "Countinue with Apple",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            LogInButton(
                onClick = {}
            )
            Spacer(modifier = Modifier.height(54.dp))
        }
    }


}

@Composable
@Preview(showBackground = true)
private fun WelcomeScreenPreview() {
    WelcomeScreen()
}