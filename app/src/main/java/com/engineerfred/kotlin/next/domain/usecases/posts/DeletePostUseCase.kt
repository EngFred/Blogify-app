package com.engineerfred.kotlin.next.domain.usecases.posts

import com.engineerfred.kotlin.next.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke( postId: String) = postRepository.deletePostById(postId)
}