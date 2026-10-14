package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.musicplayer.presentation.auth.AuthViewModel
import com.example.musicplayer.presentation.auth.screen.WelcomeScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                WelcomeScreen(viewModel = authViewModel)
            }
        }
    }
}

