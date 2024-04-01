package com.engineerfred.kotlin.next.domain.usecases.comments

import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import javax.inject.Inject

class GetPostCommentsUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    operator fun invoke( postId: String ) = commentsRepository.getPostComments(postId)
}