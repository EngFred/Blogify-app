package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.saveLoginInfoScreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.preferences.SaveLoginInfoUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.AddUserInDatabaseUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.GetUserByIdUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.RegisterUserUseCase
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val registerUserUseCase: RegisterUserUseCase,
    private val addUserInDatabaseUseCase: AddUserInDatabaseUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val saveLoginInfoUseCase: SaveLoginInfoUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(SaveInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val firstName = savedStateHandle.get<String>(Constants.FIRST_NAME_KEY)
    private val lastName = savedStateHandle.get<String>(Constants.LAST_NAME_KEY)
    private val email = savedStateHandle.get<String>(Constants.EMAIL_KEY)
    private val password = savedStateHandle.get<String>(Constants.USER_PASSWORD_KEY)

    companion object {
        private val TAG = SaveInfoViewModel::class.java.simpleName
    }

    init {
        if (  !firstName.isNullOrEmpty() && !lastName.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            Log.i(TAG, "UserInfo:\nFirst name: $firstName\nLast name: $lastName\nEmail: $email\nPassword: $password")
        }else {
            Log.i(TAG, "Some user information is missing!!")
        }
    }

    fun onEvent( event: SaveInfoUiEvents ) {
        when(event) {
            SaveInfoUiEvents.NotNowClicked -> {
                //just register user in the database
                _uiState.update {
                    it.copy(
                        isRegisteringUser = true,
                        error = null
                    )
                }
                registerUser( saveInfo = false )
            }
            SaveInfoUiEvents.SaveClicked -> {
                _uiState.update {
                    it.copy(
                        isSavingInfoAndRegisteringUser = true,
                        error = null
                    )
                }
                //store the save state using preferences, when successfully store then we add user in the database
                registerUser(saveInfo = true)
            }
        }
    }

    private fun registerUser( saveInfo: Boolean ) = viewModelScope.launch( Dispatchers.IO ) {
        if ( saveInfo ) {
            Log.d(TAG, "Saving user info...")
            saveLoginInfoUseCase.invoke(true)
            Log.d(TAG, "Registering user...")
            val task = registerUserUseCase.invoke( email!!, password!! )
            when(task){
                is Response.Failure -> {
                    Log.e(TAG, "Error registering user.")
                    _uiState.update {
                        it.copy(
                            isRegisteringUser = false,
                            error = task.exception
                        )
                    }
                }
                is Response.Success -> {
                    Log.d(TAG, "Adding user in database...")
                    _uiState.update {
                        it.copy(
                            isRegisteringUser = false,
                            isAddingUserInDatabase = true //show a progress indicator on the screen
                        )
                    }
                    val user = User(
                        id = task.data.user!!.uid,
                        lastName = lastName!!,
                        firstName = firstName!!,
                        email = email,
                        joinDate = System.currentTimeMillis()
                    )
                    val task2 = addUserInDatabaseUseCase.invoke( user )
                    when( task2 ) {
                        is Response.Failure -> {
                            Log.e(TAG, "Error adding user in database.")
                            _uiState.update {
                                it.copy(
                                    isAddingUserInDatabase = false,
                                    isRegisteringUser = false,
                                    isSavingInfoAndRegisteringUser = false,
                                    error = task2.exception
                                )
                            }
                        }
                        is Response.Success -> {
                            Log.d(TAG, "Getting user by id...")
                            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                            getUserByIdUseCase.invoke( currentUserId ).collectLatest { apiResponse ->
                                when(apiResponse){
                                    is Response.Failure -> {
                                        Log.e(TAG, "Error getting user by id")
                                        _uiState.update {
                                            it.copy(
                                                isAddingUserInDatabase = false,
                                                error = apiResponse.exception
                                            )
                                        }
                                    }
                                    is Response.Success -> {
                                        _uiState.update {
                                            it.copy(
                                                user = apiResponse.data
                                            )
                                        }
                                        Log.i(TAG, "Everything was successful!!")
                                    }
                                    Response.Undefined -> Unit
                                }
                            }
                        }
                        Response.Undefined -> Unit
                    }
                }
                Response.Undefined -> Unit
            }
        } else {
            Log.d(TAG, "Registering user...")
            val task = registerUserUseCase.invoke( email!!, password!! )
            when(task){
                is Response.Failure -> {
                    Log.e(TAG, "Error Registering user")
                    _uiState.update {
                        it.copy(
                            isRegisteringUser = false,
                            error = task.exception
                        )
                    }
                }
                is Response.Success -> {
                    Log.d(TAG, "Adding user in database")
                    _uiState.update {
                        it.copy(
                            isRegisteringUser = false,
                            isAddingUserInDatabase = true //show a progress indicator on the screen
                        )
                    }
                    val user = User(
                        id = task.data.user!!.uid,
                        lastName = lastName!!,
                        email = email,
                        firstName = firstName!!,
                        joinDate = System.currentTimeMillis()
                    )
                    val task2 = addUserInDatabaseUseCase.invoke( user )
                    when( task2 ) {
                        is Response.Failure -> {
                            Log.e(TAG, "Error Adding user...")
                            _uiState.update {
                                it.copy(
                                    isAddingUserInDatabase = false,
                                    isRegisteringUser = false,
                                    isSavingInfoAndRegisteringUser = false,
                                    error = task2.exception
                                )
                            }
                        }
                        is Response.Success -> {
                            Log.d(TAG, "Getting user by id...")
                            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                            getUserByIdUseCase.invoke( currentUserId ).collectLatest { apiResponse ->
                                when(apiResponse){
                                    is Response.Failure -> {
                                        Log.e(TAG, "Error getting user by id")
                                        _uiState.update {
                                            it.copy(
                                                isAddingUserInDatabase = false,
                                                error = apiResponse.exception
                                            )
                                        }
                                    }
                                    is Response.Success -> {
                                        Log.d(TAG, "Everything was successful!!")
                                        _uiState.update {
                                            it.copy(
                                                user = apiResponse.data
                                            )
                                        }
                                    }
                                    Response.Undefined -> Unit
                                }
                            }
                        }
                        Response.Undefined -> Unit
                    }
                }
                Response.Undefined -> Unit
            }
        }

    }
}