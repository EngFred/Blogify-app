package com.engineerfred.kotlin.next.presentation.screens.notifications

sealed class NotificationsUiEvents {
    data object RetryClicked: NotificationsUiEvents()
    data class NotificationClicked(val notificationId: String, val isNotificationRead: Boolean): NotificationsUiEvents()
}