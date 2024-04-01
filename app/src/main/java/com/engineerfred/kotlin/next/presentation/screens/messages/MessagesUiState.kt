package com.engineerfred.kotlin.next.presentation.screens.messages

import com.engineerfred.kotlin.next.domain.model.User

data class MessagesUiState(
    val friends: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val loadError: String? = null
)