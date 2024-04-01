package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke( userId: String ) = authRepository.getUserById(userId)
}