package com.engineerfred.kotlin.next.domain.usecases.users

import android.content.Context
import com.engineerfred.kotlin.next.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke( userId: String, newProfileImageUrl: String, context: Context ) = userRepository.updateUserProfilePicture( userId, newProfileImageUrl, context )
}