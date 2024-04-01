package com.engineerfred.kotlin.next.presentation.screens.settings

sealed class SettingsUiEvents {
    data object SaveUserLoginInfoStateChanged: SettingsUiEvents()
    data object ThemeChanged: SettingsUiEvents()
}