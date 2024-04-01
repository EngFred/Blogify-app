package com.engineerfred.kotlin.next.domain.repository

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun addNotification( notification: Notification ) : Response<Any>
    suspend fun deleteNotification( notificationId: String ) : Response<Any>
    suspend fun updateNotification( notificationId: String )
    fun getUserNotifications( userId: String ) : Flow<Response<List<Notification>>>
}