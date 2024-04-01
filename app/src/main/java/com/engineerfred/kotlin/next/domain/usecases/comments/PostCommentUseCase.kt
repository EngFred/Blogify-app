package com.engineerfred.kotlin.next.domain.usecases.comments

import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import javax.inject.Inject

class PostCommentUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(comment: Comment, postId: String, notification: Notification?) = commentsRepository.addComment( comment, postId, notification )
}