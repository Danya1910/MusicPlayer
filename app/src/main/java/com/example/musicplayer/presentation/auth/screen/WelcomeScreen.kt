package com.example.musicplayer.presentation.auth.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.auth.AuthViewModel
import com.example.musicplayer.presentation.auth.component.LogInButton
import com.example.musicplayer.presentation.auth.component.NextButton
import com.example.musicplayer.presentation.auth.component.SocialMediaAccountButton
import com.example.musicplayer.presentation.auth.component.WelcomeButton
import com.example.musicplayer.presentation.auth.component.WelcomeInputField
import com.example.musicplayer.presentation.auth.component.WelcomeTopBar
import com.example.musicplayer.ui.theme.DeepBlack
import com.example.musicplayer.ui.theme.SpotifyGreen
import com.example.musicplayer.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    var isSignUpMode by remember { mutableStateOf(true) }

    val steps = remember(isSignUpMode) {
        if (isSignUpMode) {
            listOf("Welcome", "Email", "Name", "Password")
        } else {
            listOf("Welcome", "Email", "Password")
        }
    }

    val pagerState = rememberPagerState(pageCount = { steps.size })

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Scaffold(
        containerColor = DeepBlack,
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (steps[page]) {
                    "Welcome" -> WelcomeContent(
                        onSignUpClick = {
                            isSignUpMode = true
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1, animationSpec = tween(400))
                            }
                        },
                        onLogInClick = {
                            isSignUpMode = false
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1, animationSpec = tween(400))
                            }
                        }
                    )

                    "Email" -> EmailStep(
                        email = email,
                        onEmailChange = { email = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                }
                            }
                        }
                    )

                    "Name" -> NameStep(
                        name = name,
                        onNameChange = { name = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                }
                            }
                        },
                    )

                    "Password" -> PasswordStep(
                        password = password,
                        onPasswordChange = { password = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                } else {
                                    if (isSignUpMode) {
                                        viewModel.signUp(email, password, name)
                                    } else {
                                        viewModel.signIn(email, password)
                                    }
                                    onAuthSuccess()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun WelcomeContent(
    onLogInClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DeepBlack,
            )
    ) {
        Image(
            painter = painterResource(R.drawable.welcome_bg),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 46.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                tint = White,
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Million of Songs.\nFree on Spotify.",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = White,
            )
            Spacer(modifier = Modifier.height(30.dp))
            WelcomeButton(
                text = "Sign up free",
                color = SpotifyGreen,
                onClick = {
                    onSignUpClick()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_google_logo,
                text = "Countinue with Google",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_facebook_logo,
                text = "Countinue with Facebook",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialMediaAccountButton(
                icon = R.drawable.ic_apple_logo,
                text = "Countinue with Apple",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            LogInButton(
                onClick = {
                    onLogInClick()
                }
            )
            Spacer(modifier = Modifier.height(54.dp))
        }
    }
}


@Composable
private fun EmailStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        WelcomeTopBar(
            title = "Create account",
            onClick = {
                onBackClick()
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "What's your email?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            modifier = Modifier
                .padding(31.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        WelcomeInputField(
            value = email,
            hintText = "example@gmail.com",
            onValueChange = onEmailChange
        )
        Spacer(modifier = Modifier.height(62.dp))
        NextButton(
            onClick = {
                onNextClick()
            }
        )
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        WelcomeTopBar(
            title = "Create account",
            onClick = {
                onBackClick()
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "What's your name?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            modifier = Modifier
                .padding(31.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        WelcomeInputField(
            value = name,
            hintText = "",
            onValueChange = onNameChange
        )
        Spacer(modifier = Modifier.height(62.dp))
        NextButton(
            onClick = {
                onNextClick()
            }
        )

    }
}

@Composable
private fun PasswordStep(
    password: String,
    onPasswordChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        WelcomeTopBar(
            title = "Create account",
            onClick = {
                onBackClick()
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Create a password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            modifier = Modifier
                .padding(31.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        WelcomeInputField(
            value = password,
            hintText = "",
            onValueChange = onPasswordChange,
            isPassword = true,
        )
        Spacer(modifier = Modifier.height(62.dp))
        NextButton(
            onClick = {
                onNextClick()
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun WelcomeScreenPreview() {

    val coroutineScope = rememberCoroutineScope()

    var isSignUpMode by remember { mutableStateOf(true) }

    val steps = remember(isSignUpMode) {
        if (isSignUpMode) {
            listOf("Welcome", "Email", "Name", "Password")
        } else {
            listOf("Welcome", "Email", "Password")
        }
    }

    val pagerState = rememberPagerState(pageCount = { steps.size })

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Scaffold(
        containerColor = DeepBlack,
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (steps[page]) {
                    "Welcome" -> WelcomeContent(
                        onSignUpClick = {
                            isSignUpMode = true
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1, animationSpec = tween(400))
                            }
                        },
                        onLogInClick = {
                            isSignUpMode = false
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1, animationSpec = tween(400))
                            }
                        }
                    )

                    "Email" -> EmailStep(
                        email = email,
                        onEmailChange = { email = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                } else {
                                    if (isSignUpMode) {
                                        //viewModel.signUp(email, password, name)
                                    } else {
                                        //viewModel.signIn(email, password)
                                    }
                                    //onAuthSuccess()
                                }
                            }
                        }
                    )

                    "Name" -> NameStep(
                        name = name,
                        onNameChange = { name = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                } else {
                                    if (isSignUpMode) {
                                        //viewModel.signUp(email, password, name)
                                    } else {
                                        //viewModel.signIn(email, password)
                                    }
                                    //onAuthSuccess()
                                }
                            }
                        },
                    )

                    "Password" -> PasswordStep(
                        password = password,
                        onPasswordChange = { password = it },
                        onBackClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < steps.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(400)
                                    )
                                } else {
                                    if (isSignUpMode) {
                                        //viewModel.signUp(email, password, name)
                                    } else {
                                        //viewModel.signIn(email, password)
                                    }
                                    //onAuthSuccess()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}