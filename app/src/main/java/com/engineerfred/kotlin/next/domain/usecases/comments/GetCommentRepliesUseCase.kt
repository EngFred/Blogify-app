package com.engineerfred.kotlin.next.domain.usecases.comments

import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import javax.inject.Inject

class GetCommentRepliesUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    operator fun invoke( commentId: String ) = commentsRepository.getCommentReplies( commentId )
}