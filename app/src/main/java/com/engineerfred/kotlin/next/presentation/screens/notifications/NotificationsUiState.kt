package com.engineerfred.kotlin.next.presentation.screens.notifications

import com.engineerfred.kotlin.next.domain.model.Notification

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val notifications: List<Notification> = emptyList()
)