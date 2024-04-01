package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, firstName: String, lastName: String, about: String?) =
        userRepository.updateUserInfo( userId, firstName, lastName, about )
}