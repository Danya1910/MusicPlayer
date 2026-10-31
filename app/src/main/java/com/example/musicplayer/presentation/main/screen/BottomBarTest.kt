package com.example.musicplayer.presentation.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.R
import com.example.musicplayer.presentation.navigation.Screen
import com.example.musicplayer.ui.theme.Gray
import com.example.musicplayer.ui.theme.White


@Composable
fun BottomBarTest(
    navController: NavController,
    pagerState: PagerState,
    onItemClick: (Int) -> Unit,
) {

    val bottomBarItems = listOf(
        BottomBarDataTest(
            icon = R.drawable.ic_home,
            text = "Home",
            route = 0,
        ),
        BottomBarDataTest(
            icon = R.drawable.ic_search,
            text = "Search",
            route = 1
        ),
        BottomBarDataTest(
            icon = R.drawable.ic_library,
            text = "Library",
            route = 2,
        ),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination



    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
    ) {
        bottomBarItems.forEach { item ->

            val isSelected = item.route == pagerState.currentPage


            val itemColor by animateColorAsState(
                targetValue = if(isSelected) White else Gray,
                animationSpec = tween(250, easing = FastOutSlowInEasing),
            )

            BottomBarItem(
                icon = item.icon,
                text = item.text,
                color = itemColor,
                modifier = Modifier.weight(1f),
                onClick = {
                    onItemClick(item.route)
                }
            )
        }
    }

}

data class BottomBarDataTest(
    val icon: Int,
    val text: String,
    val route: Int,
)

@Composable
private fun BottomBarItem(
    icon: Int,
    text: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable{
                onClick()
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = color,
            )
        }
    }
}