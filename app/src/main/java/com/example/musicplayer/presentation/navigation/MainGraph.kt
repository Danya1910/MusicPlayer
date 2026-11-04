package com.example.musicplayer.presentation.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.screen.MainRootPagerScreen

fun NavGraphBuilder.mainGraph(
    navController: NavController,
) {

    navigation<Screen.MainGraph>(startDestination = Screen.Pager) {

        composable<Screen.Pager> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.MainGraph>()
            }
            val mainViewModel: MainViewModel = hiltViewModel(parentEntry)

            MainRootPagerScreen(
                mainViewModel = mainViewModel,
                rootNavController = navController,
            )
        }
    }


}