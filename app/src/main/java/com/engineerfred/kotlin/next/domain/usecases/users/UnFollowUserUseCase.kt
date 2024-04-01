package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.repository.UserRepository
import javax.inject.Inject

class UnFollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(
        currentUserId: String,
        otherUserId: String,
        notificationId: String?
    ) = userRepository.unFollowUser( currentUserId, otherUserId, notificationId )
}