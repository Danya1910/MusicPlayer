package com.example.musicplayer.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.auth.util.AsteriskVisualTransformation
import com.example.musicplayer.presentation.main.SearchViewModel
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) { }

}

@Composable
private fun SearchField(
    value: String,
    onQueryChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    hintText: String,
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 31.dp)
            .height(56.dp)
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(5.dp)
            )
            .background(
                color = White
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onQueryChange,
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Black
            ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_for_field),
                        contentDescription = null,
                        tint = Black,
                        modifier = Modifier.size(32.dp),
                    )
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 12.dp)
                    ) {
                        if (value.isEmpty() && hintText.isNotEmpty()) {
                            Text(
                                text = hintText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray
                            )
                        }
                        innerTextField()
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }

}