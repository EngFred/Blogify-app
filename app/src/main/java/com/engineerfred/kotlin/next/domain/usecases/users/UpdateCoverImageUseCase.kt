package com.engineerfred.kotlin.next.domain.usecases.users

import android.content.Context
import com.engineerfred.kotlin.next.domain.repository.UserRepository
import javax.inject.Inject

class UpdateCoverImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke( userId: String, newCoverImageUrl: String, context: Context ) = userRepository.updateUserCoverPicture( userId, newCoverImageUrl, context )
}