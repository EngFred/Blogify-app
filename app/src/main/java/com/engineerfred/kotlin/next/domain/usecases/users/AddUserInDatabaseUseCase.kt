package com.engineerfred.kotlin.next.domain.usecases.users

import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import javax.inject.Inject

class AddUserInDatabaseUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke( user: User ) = authRepository.addUserInDatabase(user)
}