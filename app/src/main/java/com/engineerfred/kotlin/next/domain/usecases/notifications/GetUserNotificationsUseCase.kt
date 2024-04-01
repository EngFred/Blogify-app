package com.engineerfred.kotlin.next.domain.usecases.notifications

import com.engineerfred.kotlin.next.domain.repository.NotificationRepository
import javax.inject.Inject

class GetUserNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String) = notificationRepository.getUserNotifications(userId)
}