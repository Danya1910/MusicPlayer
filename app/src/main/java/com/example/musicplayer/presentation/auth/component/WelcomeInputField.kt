package com.example.musicplayer.presentation.auth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.presentation.auth.util.AsteriskVisualTransformation
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray

@Composable
fun WelcomeInputField(
    value: String,
    hintText: String?,
    onValueChange: (String) -> Unit,
    isPassword: Boolean? = false
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 31.dp)
            .height(51.dp)
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(5.dp)
            )
            .background(
                color = Gray
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Black
            ),
            visualTransformation = if(isPassword == true)
                AsteriskVisualTransformation()
            else
                VisualTransformation.None,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                    .fillMaxSize()
                        .padding(start = 12.dp)
                ) {
                    if (value.isEmpty() && !hintText.isNullOrEmpty()) {
                        Text(
                            text = hintText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}