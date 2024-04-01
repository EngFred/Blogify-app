package com.engineerfred.kotlin.next.data.remote

import android.util.Log
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : AuthRepository {

    companion object {
        private val TAG = AuthRepositoryImpl::class.java.simpleName
    }

    override suspend fun loginUser(email: String, password: String): Response<AuthResult> {
        return try {
            val task = auth.signInWithEmailAndPassword( email, password ).await()
            Response.Success(task)
        }catch (ex: Exception) {
            if (  ex is FirebaseAuthInvalidCredentialsException) {
                Response.Failure("The provided credentials are incorrect")
            }
            Response.Failure("$ex")
        }
    }

    override suspend fun registerUser(email: String, password: String): Response<AuthResult> {
        return try {
            val task = auth.createUserWithEmailAndPassword( email, password ).await()
            Response.Success(task)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override suspend fun addUserInDatabase(user: User): Response<Any> {
        return try {
            val task = database.collection(Constants.USERS_COLLECTION).document(user.id).set(user)
            Response.Success(task)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override fun getUserById(userId: String): Flow<Response<User?>> {
        return callbackFlow{
            trySend(Response.Undefined)
            val task = database.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        val user = value.toObject(User::class.java)
                        trySend(Response.Success(user))
                        Log.v("#!", "Success!! Got user.")
                    }

                    if ( error != null ) {
                        trySend( Response.Failure("$error") )
                        Log.v("#!", "Error!! $error")
                        return@addSnapshotListener
                    }
                }
            awaitClose { task.remove() }
        }.catch {
            emit(Response.Failure("$it"))
        }.flowOn( Dispatchers.IO )
    }

    override fun getUsers(): Flow<Response<MutableList<User>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.USERS_COLLECTION).addSnapshotListener { value, error ->
                if ( error != null ) {
                    trySend(Response.Failure(error.toString()))
                }
                if ( value != null ) {
                    val users = value.toObjects(User::class.java)
                    trySend(Response.Success(users))
                }
            }
            awaitClose { task.remove() }
        }.catch {
            Log.v(TAG, "ERROR Getting users: $it")
            emit(Response.Failure("$it"))
        }.flowOn( Dispatchers.IO )
    }

    override suspend fun logoutUser(): Response<Any> {
        return try {
            val task = auth.signOut()
            Response.Success(task)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }
}