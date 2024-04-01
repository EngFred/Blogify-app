package com.engineerfred.kotlin.next.domain.repository

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun addPost( post: Post, context: Context ) : Response<Any>
    suspend fun likePost(userId: String, postId: String, notification: Notification?)
    suspend fun unLikePost(userId: String, postId: String, notificationId: String?)
    fun getPosts() : Flow<Response<List<Post>>>
    fun getUserPosts(userId: String) : Flow<Response<MutableList<Post>>>
    fun getPostById( postId: String ) : Flow<Response<Post?>>
    suspend fun deletePostById( postId: String ) : Response<Any>
}