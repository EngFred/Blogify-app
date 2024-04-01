package com.engineerfred.kotlin.next.domain.usecases.notifications

import com.engineerfred.kotlin.next.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke( notificationId: String ) = notificationRepository.updateNotification(notificationId)
}