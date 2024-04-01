package com.engineerfred.kotlin.next.presentation.screens.addFeeling

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddFeelingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddFeelingUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent( event: AddFeelingUiEvent ) {
        when(event) {
            is AddFeelingUiEvent.FeelingSelected -> {
                _uiState.update {
                    it.copy(
                        feeling = event.feeling
                    )
                }
            }
        }
    }
}