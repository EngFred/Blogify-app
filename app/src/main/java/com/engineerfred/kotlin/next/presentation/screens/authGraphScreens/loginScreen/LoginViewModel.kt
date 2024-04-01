package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.usecases.preferences.GetLoginInfoUseCase
import com.engineerfred.kotlin.next.domain.usecases.preferences.GetThemeUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.GetUserByIdUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.LogInUserUseCase
import com.engineerfred.kotlin.next.utils.Response
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
class LoginViewModel @Inject constructor(
    private val getLoginInfoUseCase: GetLoginInfoUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val logInUserUseCase: LogInUserUseCase,
    private val getThemeUseCase: GetThemeUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    private val loggedInUser = FirebaseAuth.getInstance().currentUser

    private val _theme = MutableStateFlow(false)
    val theme = _theme.asStateFlow()

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }

    init {
        getTheme()
    }

    fun onEvent( event: LoginUiEvents ) {
        _uiState.update {
            it.copy(
                loginError = null,
                isInitializing = false,
                initError = null,
                isLoggingIn = false
            )
        }
        when( event ) {
            is LoginUiEvents.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        emailTextValue = event.email
                    )
                }
            }
            LoginUiEvents.LoginButtonClicked -> {
                _uiState.update {
                    it.copy(
                        isLoggingIn = true
                    )
                }
                loginUser()
            }
            is LoginUiEvents.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        passwordTextValue = event.password
                    )
                }
            }
        }
    }


    init {
        initializeApp()
    }

    private fun loginUser() = viewModelScope.launch( Dispatchers.IO ) {
        val task = logInUserUseCase.invoke( _uiState.value.emailTextValue, _uiState.value.passwordTextValue )

        when( task ) {
            is Response.Failure -> {
                _uiState.update {
                    it.copy(
                        loginError = task.exception,
                        isInitializing = false,
                        isLoggingIn = false
                    )
                }
            }
            is Response.Success -> {
                Log.d(TAG, "Getting user by id from database...")
                getUserByIdUseCase.invoke( task.data.user!!.uid ).collectLatest { apiResponse ->
                    when( apiResponse ) {
                        is Response.Failure -> {
                            Log.e(TAG, "Error Getting user from database.")
                            _uiState.update {
                                it.copy(
                                    loginError = apiResponse.exception,
                                    isLoggingIn = false
                                )
                            }
                        }
                        is Response.Success -> {
                            Log.i(TAG, "User has found and retrieved from the database!!")
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


    private fun initializeApp() = viewModelScope.launch( Dispatchers.IO ) {
        if ( loggedInUser != null ) {
            Log.d(TAG, "Getting saved login user info...")
            getLoginInfoUseCase.invoke().collectLatest {
                if ( it ) {
                    Log.d(TAG, "Getting user by id from database...")
                    getUserByIdUseCase.invoke( loggedInUser.uid ).collectLatest { apiResponse ->
                        when( apiResponse ) {
                            is Response.Failure -> {
                                Log.e(TAG, "Error Getting user from database.")
                                _uiState.update {
                                    it.copy(
                                        initError = apiResponse.exception,
                                        isInitializing = false
                                    )
                                }
                            }
                            is Response.Success -> {
                                Log.i(TAG, "User has been found and retrieved from the database!!")
                                _uiState.update {
                                    it.copy(
                                        user = apiResponse.data
                                    )
                                }
                            }
                            Response.Undefined -> Unit
                        }
                    }
                } else {
                    Log.i(TAG, "Login info was not saved but a user id already exists!!")
                    _uiState.update {
                        it.copy(
                            isInitializing = false
                        )
                    }
                }
            }
        } else {
            Log.i(TAG, "Login info was not saved and also there's no any logged in user into the app!!")
            _uiState.update {
                it.copy(
                    isInitializing = false
                )
            }
        }
    }

    private fun getTheme() = viewModelScope.launch {
        getThemeUseCase.invoke().collectLatest {  theme ->
            _uiState.update {
                it.copy(
                    isDarkTheme = theme
                )
            }
        }
    }
}