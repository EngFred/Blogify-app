package com.engineerfred.kotlin.next.domain.repository

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers() : Flow<Response<List<User>>>

    suspend fun updateUserProfilePicture( userId: String, profileImageUrl: String, context: Context ) : Response<Any>
    suspend fun updateUserInfo(userId: String, firstName: String, lastName: String, about: String?) : Response<Any>
    suspend fun updateUserCoverPicture( userId: String, coverImageUrl: String, context: Context ) : Response<Any>

    suspend fun followUser( currentUserId: String, otherUserId: String, notification: Notification ) : Response<Any>
    suspend fun followBackUser( currentUserId: String, otherUserId: String, notification: Notification ) : Response<Any>
    suspend fun unFollowUser( currentUserId: String, otherUserId: String, notificationId: String?) : Response<Any>

}