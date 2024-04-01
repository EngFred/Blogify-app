package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.saveLoginInfoScreen

import com.engineerfred.kotlin.next.domain.model.User

data class SaveInfoUiState(
    val isSavingInfoAndRegisteringUser: Boolean = false,
    val isRegisteringUser: Boolean = false,
    val isAddingUserInDatabase: Boolean = false,

    val error: String? = null,
    val user: User? = null
)
