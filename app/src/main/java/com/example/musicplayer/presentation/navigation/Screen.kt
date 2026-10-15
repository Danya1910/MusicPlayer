package com.example.musicplayer.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable data object AuthGraph: Screen
    @Serializable data object MainGraph: Screen

    @Serializable data object Welcome: Screen
    @Serializable data object Home: Screen

}