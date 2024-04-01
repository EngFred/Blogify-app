package com.engineerfred.kotlin.next.domain.repository

import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.utils.Response
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser( email: String, password: String ) : Response<AuthResult>
    suspend fun registerUser( email: String, password: String ) : Response<AuthResult>
    suspend fun addUserInDatabase(user: User ) : Response<Any>
    fun getUserById( userId: String ) : Flow<Response<User?>>
    fun getUsers(): Flow<Response<MutableList<User>>>
    suspend fun logoutUser() : Response<Any>
}