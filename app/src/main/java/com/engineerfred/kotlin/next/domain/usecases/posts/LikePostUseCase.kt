package com.engineerfred.kotlin.next.domain.usecases.posts

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke( userId: String, postId: String, notification: Notification?) = postRepository.likePost(userId, postId, notification)
}