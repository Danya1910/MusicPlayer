package com.example.musicplayer.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.mainGraph() {

    navigation<Screen.MainGraph>(startDestination = Screen.Home) {
        composable<Screen.Home> {
            Text(
                text = "Главный экран",
                fontSize = 22.sp
            )
        }
     }

}