package com.engineerfred.kotlin.next.presentation.screens.addLocation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddLocationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddLocationUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent( event: AddLocationUiEvent ) {
        when(event) {
            is AddLocationUiEvent.LocationChanged -> {
                _uiState.update {
                    it.copy(
                        locationText = event.location
                    )
                }
            }
        }
    }
}