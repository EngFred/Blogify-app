package com.engineerfred.kotlin.next.domain.usecases.notifications

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.repository.NotificationRepository
import javax.inject.Inject

class AddNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke( notification: Notification ) = notificationRepository.addNotification( notification )
}