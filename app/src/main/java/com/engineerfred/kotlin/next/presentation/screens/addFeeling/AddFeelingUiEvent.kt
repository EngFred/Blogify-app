package com.engineerfred.kotlin.next.presentation.screens.addFeeling

sealed class AddFeelingUiEvent {
    data class FeelingSelected(  val feeling: String ) : AddFeelingUiEvent()
}