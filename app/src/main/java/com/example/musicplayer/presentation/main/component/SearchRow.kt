package com.example.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.DarkGray
import com.example.musicplayer.ui.theme.White


@Composable
fun SearchRow(
    value: String,
    onValueChange: (String) -> Unit,
    onCancelClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 17.dp, end = 12.dp)
    ) {
        SearchField(
            value = value,
            onValueChange = onValueChange,
        )
        Spacer(modifier = Modifier.width(25.dp))
        Text(
            text = "Cancel",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = White,
            modifier = Modifier
                .clickable{
                    onCancelClick()
                }
        )
    }

}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
            .clip(
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = Color(0xFF282828)
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Black
            ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp)
                ) {
                    if (value.isEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_for_field),
                            contentDescription = null,
                            tint = White,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
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