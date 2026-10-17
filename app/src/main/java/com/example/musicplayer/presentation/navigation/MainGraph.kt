package com.example.musicplayer.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.screen.MainScreen

fun NavGraphBuilder.mainGraph() {

    navigation<Screen.MainGraph>(startDestination = Screen.Main) {
        composable<Screen.Main> {
            val mainViewModel: MainViewModel = hiltViewModel()

            MainScreen(
                mainViewModel = mainViewModel,
            )
        }
     }

}