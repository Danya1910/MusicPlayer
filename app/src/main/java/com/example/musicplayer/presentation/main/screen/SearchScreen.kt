package com.example.musicplayer.presentation.main.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.main.SearchViewModel
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White


@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
) {

    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()

    ) {
        Spacer(modifier = Modifier.height(20.dp))

        SearchField(
            value = query,
            onQueryChange = {
                query = it
                searchViewModel.onQueryChanged(
                    query = it
                )
            },
            onCancelClick = {},
            hintText = "What do you want to listen to?"
        )
    }

}

@Composable
private fun SearchField(
    value: String,
    onQueryChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    hintText: String,
) {

    val showIcon = value.isBlank()

    val rowOffset by animateDpAsState(
        targetValue = if (showIcon) 0.dp else -(12.dp + 32.dp),
        animationSpec = tween(durationMillis = 250),
        label = "rowOffset"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(x = rowOffset)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_for_field),
                        contentDescription = null,
                        tint = Black,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(32.dp)
                    )
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 12.dp)
                    ) {
                        if (value.isEmpty() && hintText.isNotEmpty()) {
                            Text(hintText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gray)
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