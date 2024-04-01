package com.engineerfred.kotlin.next.presentation.screens.editProfile

sealed class EditProfileUiEvents {
    data class FirstNameChanged( val firstName: String ) : EditProfileUiEvents()
    data class LastNameChanged( val lastName: String ) : EditProfileUiEvents()
    data class AboutChanged( val about: String ) : EditProfileUiEvents()
    data object UpdateButtonClicked: EditProfileUiEvents()
}