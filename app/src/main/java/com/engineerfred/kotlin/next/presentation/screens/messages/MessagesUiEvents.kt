package com.engineerfred.kotlin.next.presentation.screens.messages

sealed class MessagesUiEvents {
    data object RetryClicked: MessagesUiEvents()
}