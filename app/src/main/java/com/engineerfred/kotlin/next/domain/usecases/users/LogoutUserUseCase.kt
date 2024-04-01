package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logoutUser()
}