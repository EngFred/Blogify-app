package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen

import com.engineerfred.kotlin.next.domain.model.User

data class LoginUiState(
    val isInitializing: Boolean = true,
    val isLoggingIn: Boolean = false,
    val initError: String? = null,
    val loginError: String? = null,
    val passwordTextValue: String = "",
    val emailTextValue: String = "",

    val user: User? = null,
    val isDarkTheme: Boolean? = null
)
