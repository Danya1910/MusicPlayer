package com.example.musicplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R

// Set of Material typography styles to start with

val AvenirNextFontFamily = FontFamily(
    Font(R.font.avenir_next_regular, FontWeight.Normal),    // Regular (400)
    Font(R.font.avenir_next_medium, FontWeight.Medium),    // Medium (500)
    Font(R.font.avenir_next_demi, FontWeight.SemiBold),     // Demi (600)
    Font(R.font.avenir_next_bold, FontWeight.Bold)          // Bold (700)
)

// 2. Настраиваем глобальную типографику приложения
val Typography = Typography(
    // Крупные заголовки (С возвращением, Название трека в плеере)
    headlineLarge = TextStyle(
        fontFamily = AvenirNextFontFamily,
        fontWeight = FontWeight.Bold, // Автоматически подтянет avenir_next_bold
        fontSize = 28.sp
    ),
    // Подзаголовки или имена артистов (Medium / Demi)
    titleMedium = TextStyle(
        fontFamily = AvenirNextFontFamily,
        fontWeight = FontWeight.SemiBold, // Автоматически подтянет avenir_next_demi
        fontSize = 18.sp
    ),
    // Текст в кнопках и полях ввода
    bodyLarge = TextStyle(
        fontFamily = AvenirNextFontFamily,
        fontWeight = FontWeight.Normal, // Автоматически подтянет avenir_next_regular
        fontSize = 16.sp
    )
)
