package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen

sealed class LoginUiEvents {
    data class EmailChanged( val email: String ) : LoginUiEvents()
    data class PasswordChanged( val password: String ) : LoginUiEvents()
    data object LoginButtonClicked: LoginUiEvents()
}