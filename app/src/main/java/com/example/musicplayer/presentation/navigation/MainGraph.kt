package com.example.musicplayer.presentation.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.musicplayer.presentation.main.MainViewModel
import com.example.musicplayer.presentation.main.screen.LibraryScreen
import com.example.musicplayer.presentation.main.screen.MainScreen
import com.example.musicplayer.presentation.main.screen.MainTest

fun NavGraphBuilder.mainGraph(
    navController: NavController,
) {

    navigation<Screen.MainGraph>(startDestination = Screen.MainTest) {

        composable<Screen.MainTest> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.MainGraph>()
            }
            val mainViewModel: MainViewModel = hiltViewModel(parentEntry)

            MainTest(
                mainViewModel = mainViewModel,
                navController = navController,
            )
        }

        composable<Screen.Main> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.MainGraph>()
            }
            val mainViewModel: MainViewModel = hiltViewModel(parentEntry)

            MainScreen(
                mainViewModel = mainViewModel,
                navController = navController,
            )
        }

        composable<Screen.Library> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.MainGraph>()
            }
            val mainViewModel: MainViewModel = hiltViewModel(parentEntry)

            LibraryScreen(
                mainViewModel = mainViewModel,
                navController = navController,
            )
        }

    }


}