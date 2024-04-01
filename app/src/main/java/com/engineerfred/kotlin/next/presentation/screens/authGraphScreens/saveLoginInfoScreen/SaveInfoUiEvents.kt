package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.saveLoginInfoScreen

sealed class SaveInfoUiEvents {
    data object SaveClicked : SaveInfoUiEvents()
    data object NotNowClicked : SaveInfoUiEvents()
}