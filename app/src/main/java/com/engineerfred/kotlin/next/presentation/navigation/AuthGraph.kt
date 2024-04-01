package com.engineerfred.kotlin.next.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.emailScreen.EnterEmailScreen
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.joinNextScreen.JoinNextScreen
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.LoginScreen
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.LoginViewModel
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.nameScreen.EnterNameScreen
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.passwordScreen.EnterPasswordScreen
import com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.saveLoginInfoScreen.SaveLoginInfoScreen
import com.engineerfred.kotlin.next.utils.Constants

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    commonViewModel: CommonViewModel,
    loginViewModel: LoginViewModel,
    isDarkTheme: Boolean
) {
    navigation(
        startDestination = ScreenRoutes.Login.destination,
        route = Graphs.Auth.name
    ) {


        composable(
            route = ScreenRoutes.Login.destination,
            enterTransition = { slideInHorizontally() }
        ) {
            LoginScreen(
                onCreateAccount = {  navController.navigate(ScreenRoutes.JoinNext.destination) {
                    launchSingleTop = true
                }
            }, viewModel = loginViewModel, isDarkTheme = isDarkTheme)
        }

        composable(
            route = ScreenRoutes.JoinNext.destination,
            enterTransition = { slideInHorizontally() }
        ) {
            JoinNextScreen(onBackClicked = { navController.navigateUp()}, onGetStartedClicked = {
                navController.navigate( ScreenRoutes.EnterName.destination ) {
                    launchSingleTop = true
                }
            }, onAlreadyHaveAccountClicked = {
                navController.navigate( ScreenRoutes.Login.destination ){
                    popUpTo(ScreenRoutes.Login.destination)
                    launchSingleTop = true
                }
            }, isDarkTheme = isDarkTheme)
        }

        composable(
            route = ScreenRoutes.EnterName.destination,
            enterTransition = { slideInHorizontally() }
        ) {
            EnterNameScreen(
                onBackClicked = {
                  navController.navigateUp()
            }, onNextClicked = { firstName, lastName ->
                navController.navigate( "${ScreenRoutes.EnterEmail.destination}/$firstName/$lastName"){
                    launchSingleTop = true
                }
            }, onAlreadyHaveAccountClicked = {
                    navController.navigate( ScreenRoutes.Login.destination ){
                        popUpTo(ScreenRoutes.Login.destination)
                        launchSingleTop = true
                    }
                }, isDarkTheme = isDarkTheme
            )
        }

        composable(
            route = "${ScreenRoutes.EnterEmail.destination}/{${Constants.FIRST_NAME_KEY}}/{${Constants.LAST_NAME_KEY}}",
            arguments = listOf(
                navArgument(Constants.FIRST_NAME_KEY){ NavType.StringType },
                navArgument(Constants.LAST_NAME_KEY){ NavType.StringType }
            ),
            enterTransition = { slideInHorizontally() }
        ) {
            val firstName = it.arguments?.getString( Constants.FIRST_NAME_KEY )!!
            val lastName = it.arguments?.getString( Constants.LAST_NAME_KEY )!!

            EnterEmailScreen(
                lastName = lastName,
                firstName = firstName,
                onBackClicked = { navController.navigateUp() },
                onNextClicked = { firstName, lastName, email ->
                    navController.navigate("${ScreenRoutes.CreatePassword.destination}/$firstName/$lastName/$email"){
                        launchSingleTop = true
                    }
                }, onAlreadyHaveAccountClicked = {
                    navController.navigate( ScreenRoutes.Login.destination ){
                        popUpTo(ScreenRoutes.Login.destination)
                        launchSingleTop = true
                    }
                }, isDarkTheme = isDarkTheme
            )
        }


        composable(
            route = "${ScreenRoutes.CreatePassword.destination}/{${Constants.FIRST_NAME_KEY}}/{${Constants.LAST_NAME_KEY}}/{${Constants.EMAIL_KEY}}",
            arguments = listOf(
                navArgument(Constants.FIRST_NAME_KEY){ NavType.StringType },
                navArgument(Constants.LAST_NAME_KEY){ NavType.StringType },
                navArgument(Constants.EMAIL_KEY){ NavType.StringType },
            ),
            enterTransition = { slideInHorizontally() }
        ) {
            val firstName = it.arguments?.getString( Constants.FIRST_NAME_KEY )!!
            val lastName = it.arguments?.getString( Constants.LAST_NAME_KEY )!!
            val email = it.arguments?.getString( Constants.EMAIL_KEY )!!

            EnterPasswordScreen(
                lastName = lastName,
                firstName = firstName,
                email = email,
                onBackClicked = { navController.navigateUp() },
                onNextClicked = { firstName, lastName, email, password ->
                    navController.navigate("${ScreenRoutes.SaveLoginInfo.destination}/$firstName/$lastName/$email/$password"){
                        launchSingleTop = true
                    }
                }, onAlreadyHaveAccountClicked = {
                    navController.navigate( ScreenRoutes.Login.destination ){
                        popUpTo(ScreenRoutes.Login.destination)
                        launchSingleTop = true
                    }
                }, isDarkTheme = isDarkTheme
            )
        }

        composable(
            route = "${ScreenRoutes.SaveLoginInfo.destination}/{${Constants.FIRST_NAME_KEY}}/{${Constants.LAST_NAME_KEY}}/{${Constants.EMAIL_KEY}}/{${Constants.USER_PASSWORD_KEY}}",
            arguments = listOf(
                navArgument(Constants.FIRST_NAME_KEY){ NavType.StringType },
                navArgument(Constants.LAST_NAME_KEY){ NavType.StringType },
                navArgument(Constants.EMAIL_KEY){ NavType.StringType },
                navArgument(Constants.USER_PASSWORD_KEY){ NavType.StringType },
            ),
            enterTransition = { slideInHorizontally() }
        ) {
            val firstName = it.arguments?.getString( Constants.FIRST_NAME_KEY )!!
            val lastName = it.arguments?.getString( Constants.LAST_NAME_KEY )!!
            val email = it.arguments?.getString( Constants.EMAIL_KEY )!!
            val userPassword = it.arguments?.getString( Constants.USER_PASSWORD_KEY )!!

            SaveLoginInfoScreen(
                lastName = lastName,
                firstName = firstName,
                email = email,
                password = userPassword,
                onBackClicked = { navController.navigateUp() },
                onAlreadyHaveAccountClicked = {
                    navController.navigate( ScreenRoutes.Login.destination ){
                        popUpTo(ScreenRoutes.Login.destination)
                        launchSingleTop = true
                    }
                },onRegistrationSuccessful = {
                    navController.navigate( Graphs.Home.name ) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }, commonViewModel = commonViewModel, isDarkTheme = isDarkTheme
            )
        }
    }
}