package com.engineerfred.kotlin.next.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.LoginViewModel
import com.engineerfred.kotlin.next.presentation.screens.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MainAppGraph(
    navController: NavHostController = rememberNavController(),
    onThemeToggled: () -> Unit,
    isDarkTheme: Boolean,
    commonViewModel: CommonViewModel,
    loginViewModel: LoginViewModel,
    startDestination: String,
    isDarkTheme2: Boolean
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        route = Graphs.Main.name
    ) {

        authGraph(navController, commonViewModel, loginViewModel, isDarkTheme2)

        composable( route = Graphs.Home.name ) {
            HomeScreen(onThemeToggled = onThemeToggled, isDarkTheme = isDarkTheme, commonViewModel = commonViewModel)
        }

    }
}