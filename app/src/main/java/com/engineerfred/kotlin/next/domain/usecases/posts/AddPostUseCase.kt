package com.engineerfred.kotlin.next.domain.usecases.posts

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.repository.PostRepository
import javax.inject.Inject

class AddPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke( post: Post, context: Context ) = postRepository.addPost( post, context )
}