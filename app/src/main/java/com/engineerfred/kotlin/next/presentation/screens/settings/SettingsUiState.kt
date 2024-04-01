package com.engineerfred.kotlin.next.presentation.screens.settings

data class SettingsUiState(
    val isSavingUserInfo: Boolean = true,
    val saveInfo: Boolean = false,
    val isDarkTheme: Boolean = false,
)