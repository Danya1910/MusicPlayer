package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White


@Composable
fun PlayListNameInput(
    onCreateClick: (String) -> Unit,
    trackName: String,
) {

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Gray,
            DarkGray,
            DeepBlack,
        )
    )

    var name by remember { mutableStateOf(trackName) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .clip(
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                brush = bgGradient,
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Give your playlist a name",
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                letterSpacing = -(0.43).sp,
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(90.dp))
            InputField(
                value = name,
                onValueChange = {
                    name = it
                }
            )
            Spacer(modifier = Modifier.height(45.dp))
            CreateButton(
                onClick = {
                    onCreateClick(
                        name
                    )
                }
            )
        }
    }

}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 46.sp,
                color = White,
                textAlign = TextAlign.Center,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false
            ),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 15.dp
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp)
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = Gray,
                )
        )
    }
}

@Composable
private fun CreateButton(
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    bottom = 60.dp
                )
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = SpotifyGreen
                )
                .padding(
                    horizontal = 28.dp,
                    vertical = 14.dp
                )
                .clickable {
                    onClick()
                }
        ) {
            Text(
                text = "Create",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Black,
            )
        }
    }
}