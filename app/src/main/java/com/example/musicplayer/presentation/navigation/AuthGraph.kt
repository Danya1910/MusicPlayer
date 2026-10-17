package com.example.musicplayer.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.musicplayer.presentation.auth.AuthViewModel
import com.example.musicplayer.presentation.auth.screen.WelcomeScreen
import androidx.hilt.navigation.compose.hiltViewModel

fun NavGraphBuilder.authGraph(
    onAuthSuccess: () -> Unit,
) {

    navigation<Screen.AuthGraph>(startDestination = Screen.Welcome) {


        composable<Screen.Welcome> {
            val authViewModel: AuthViewModel = hiltViewModel()

            WelcomeScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    onAuthSuccess()
                }
            )
        }
     }

}