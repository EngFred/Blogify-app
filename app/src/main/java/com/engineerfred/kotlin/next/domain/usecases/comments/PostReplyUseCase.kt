package com.engineerfred.kotlin.next.domain.usecases.comments

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import javax.inject.Inject

class PostReplyUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(reply: Reply, commentId: String, notification: Notification?) = commentsRepository.addReplyOnComment(reply, commentId, notification)
}