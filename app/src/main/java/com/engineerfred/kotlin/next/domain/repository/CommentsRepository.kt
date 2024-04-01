package com.engineerfred.kotlin.next.domain.repository

import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface CommentsRepository {
    suspend fun addComment(comment: Comment, postId: String, notification: Notification?)
    suspend fun addReplyOnComment(reply: Reply, commentId: String, notification: Notification?)
    fun getPostComments( postId: String ) : Flow<Response<List<Comment>>>
    fun getCommentReplies(commentId: String) : Flow<Response<List<Reply>>>
    fun getCommentById( commentId: String ) : Flow<Response<Comment?>>
}