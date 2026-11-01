package com.example.musicplayer.presentation.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface TabScreen {

    @Serializable data object Home : TabScreen
    @Serializable data object Search : TabScreen
    @Serializable data object Library : TabScreen
}