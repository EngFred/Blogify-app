package com.engineerfred.kotlin.next.data.remote

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.repository.NotificationRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : NotificationRepository {

    override suspend fun addNotification(notification: Notification): Response<Any> {
        return try {
            val task = database.collection(Constants.NOTIFICATIONS_COLLECTION).document(notification.id).set(notification).await()
            Response.Success(task)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override suspend fun deleteNotification(notificationId: String): Response<Any> {
        return try {
            val task = database.collection(Constants.NOTIFICATIONS_COLLECTION).document(notificationId).delete().await()
            Response.Success(task)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override suspend fun updateNotification(notificationId: String) {
        try {
         withContext(NonCancellable){
             val task = database
                 .collection(Constants.NOTIFICATIONS_COLLECTION)
                 .document(notificationId).update("read", true )
                 .await()
             Response.Success(task)
         }
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override fun getUserNotifications(userId: String): Flow<Response<List<Notification>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.NOTIFICATIONS_COLLECTION)
                .whereEqualTo("toUserId", userId)
                .addSnapshotListener { value, error ->
                if ( value != null ) {
                    val notifications = value.toObjects(Notification::class.java)
                    trySend(Response.Success(notifications))
                }
                if ( error != null ) {
                    return@addSnapshotListener
                }
            }
            awaitClose { task.remove() }
        }.flowOn(Dispatchers.IO).catch {
            emit(Response.Failure("$it"))
        }
    }

}