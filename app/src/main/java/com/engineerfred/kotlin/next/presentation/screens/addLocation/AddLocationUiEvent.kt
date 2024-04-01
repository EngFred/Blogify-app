package com.engineerfred.kotlin.next.presentation.screens.addLocation

sealed class AddLocationUiEvent {
    data class LocationChanged( val location: String ) : AddLocationUiEvent()
}