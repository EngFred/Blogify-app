package com.engineerfred.kotlin.next.presentation.screens.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.users.FollowBackUserUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.FollowUserUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.GetUsersUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.UnFollowUserUseCase
import com.engineerfred.kotlin.next.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unFollowUserUseCase: UnFollowUserUseCase,
    private val followBackUserUseCase: FollowBackUserUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUsers()
    }

    fun onEvent( event: PeopleUiEvents ) {
        when(event){
            PeopleUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        loadError = null,
                        isLoading = true,
                        users = mutableListOf()
                    )
                }
                getUsers()
            }

            is PeopleUiEvents.FollowedUser -> {
                followUser( event.userId )
                //_uiState.value.users.remove(event.user)
            }

            is PeopleUiEvents.FollowedBackUser -> {
                followBackUser(event.userId)
            }
            is PeopleUiEvents.UnFollowedUser -> {
                unFollowUser(event.userId)
            }
        }

    }

    private fun getUsers() = viewModelScope.launch {
        getUsersUseCase.invoke().collectLatest { apiResponse ->
            when(apiResponse){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            loadError = apiResponse.exception,
                            isLoading = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            users = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun followUser( userId: String ) = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null ) {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.Follow.name,
                fromUserName = "${_uiState.value.currentUser!!.firstName} ${_uiState.value.currentUser!!.lastName}",
                fromUserId = _uiState.value.currentUser!!.id,
                postedAt = System.currentTimeMillis(),
                toUserId = userId
            )

            val task = followUserUseCase.invoke(  _uiState.value.currentUser!!.id, userId, notification  )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            followError = task.exception,
                            followingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    followError = "Something went wrong!!"
                )
            }
        }

    }

    fun setCurrentUser(currentUser: User) {
        _uiState.update {
            it.copy(
                currentUser = currentUser
            )
        }
    }

    private fun followBackUser(userId: String) = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null ) {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                fromUserProfileImageUrl = _uiState.value.currentUser!!.profileImageUrl,
                type = NotificationType.FollowBack.name,
                fromUserName = "${_uiState.value.currentUser!!.firstName} ${_uiState.value.currentUser!!.lastName}",
                fromUserId = _uiState.value.currentUser!!.id,
                postedAt = System.currentTimeMillis(),
                toUserId = userId
            )

            val task = followBackUserUseCase.invoke(  _uiState.value.currentUser!!.id, userId, notification  )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            followError = task.exception,
                            followingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    followError = "Something went wrong!!"
                )
            }
        }

    }

    private fun unFollowUser(userId: String) = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null ) {
            val task = unFollowUserUseCase.invoke(  _uiState.value.currentUser!!.id, userId, null )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            followError = task.exception,
                            followingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    followError = "Something went wrong!!"
                )
            }
        }
    }
}