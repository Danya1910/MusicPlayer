package com.example.musicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun RootNavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
) {

    NavHost(
        navController = navController,
        startDestination = if(isUserLoggedIn) Screen.MainGraph else Screen.AuthGraph
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