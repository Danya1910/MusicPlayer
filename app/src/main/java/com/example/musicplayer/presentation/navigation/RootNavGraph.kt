package com.example.musicplayer.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.musicplayer.R
import com.example.musicplayer.presentation.splash.SplashViewModel
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.SpotifyGreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    splashViewModel: SplashViewModel = hiltViewModel(),
) {

    val startDestination by splashViewModel.startDestination.collectAsStateWithLifecycle()


    if(startDestination == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = DeepBlack
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                tint = SpotifyGreen,
            )
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            authGraph(
                onAuthSuccess = {
                    navController.navigate(Screen.MainGraph) {
                        popUpTo(Screen.AuthGraph) {
                            inclusive = true
                        }
                    }
                }
            )

            mainGraph()
        }
    }
}