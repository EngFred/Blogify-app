package com.engineerfred.kotlin.next.presentation.screens.editProfile

data class EditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val about: String? = null,
    val isUpdating: Boolean = false,
    val updateError: String? = null,
    val updateSuccessful: Boolean = false
)
