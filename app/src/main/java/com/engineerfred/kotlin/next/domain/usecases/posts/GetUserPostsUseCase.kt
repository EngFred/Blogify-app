package com.engineerfred.kotlin.next.domain.usecases.posts

import com.engineerfred.kotlin.next.domain.repository.PostRepository
import javax.inject.Inject

class GetUserPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(userId: String) = postRepository.getUserPosts(userId)
}