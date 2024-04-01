package com.engineerfred.kotlin.next.presentation

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.navigation.Graphs
import com.engineerfred.kotlin.next.presentation.navigation.MainAppGraph
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.LoginViewModel
import com.engineerfred.kotlin.next.presentation.screens.settings.SettingsViewModel
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun App(
    loginViewModel: LoginViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel = viewModel()
) {

    val loginUiState = loginViewModel.uiState.collectAsState().value

    if ( loginUiState.isDarkTheme != null ) {
        Log.i("Theme", "isDarkTheme: ${loginUiState.isDarkTheme}")
        commonViewModel.changeTheme(loginUiState.isDarkTheme)

        val startDestination = when {
            loginUiState.user != null -> {
                commonViewModel.addUser(loginUiState.user)
                Graphs.Home.name
            }
            else -> Graphs.Auth.name
        }

        var appTheme by rememberSaveable {
            mutableStateOf(loginUiState.isDarkTheme)
        }

        NextTheme( darkTheme = appTheme ) {
            MainAppGraph(
                onThemeToggled = {
                    appTheme = !appTheme
                    settingsViewModel.saveTheme(appTheme)
                },
                isDarkTheme = appTheme,
                startDestination = startDestination,
                commonViewModel = commonViewModel,
                loginViewModel = loginViewModel,
                isDarkTheme2 = appTheme
            )
        }
    } else {
        Log.i("Theme", "The theme is null!!")
        Box(modifier = Modifier
            .fillMaxSize()
            .background(DarkSlateGrey)
            .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            val view = LocalView.current
            LaunchedEffect(key1 = Unit) {
                val window = (view.context as Activity).window
                window.statusBarColor = DarkSlateGrey.toArgb()
                window.navigationBarColor = DarkSlateGrey.toArgb()
            }
            Image(
                painter = painterResource(id = R.drawable.compose),
                contentDescription = null,
                modifier = Modifier.clip(CircleShape).size(285.dp).align(Alignment.Center)
            )
            Text(text = "Next @2024", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }


}