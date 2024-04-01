package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.repository.UserRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(
        currentUserId: String,
        otherUserId: String,
        notification: Notification
    ) = userRepository.followUser( currentUserId, otherUserId, notification )
}