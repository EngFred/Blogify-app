package com.engineerfred.kotlin.next.domain.usecases.posts

import com.engineerfred.kotlin.next.domain.repository.PostRepository
import javax.inject.Inject

class UnLikePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke( userId: String, postId: String, notificationId: String?) = postRepository.unLikePost(userId, postId, notificationId)
}