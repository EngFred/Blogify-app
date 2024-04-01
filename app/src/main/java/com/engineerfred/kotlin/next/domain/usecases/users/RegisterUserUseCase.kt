package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke( email: String, password: String ) = authRepository.registerUser(email, password)
}