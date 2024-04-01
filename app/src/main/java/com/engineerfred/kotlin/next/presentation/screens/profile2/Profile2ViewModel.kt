package com.engineerfred.kotlin.next.presentation.screens.profile2

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.posts.GetUserPostsUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.LikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.UnLikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.FollowBackUserUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.FollowUserUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.GetUserByIdUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.UnFollowUserUseCase
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
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
class Profile2ViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val followBackUserUseCase: FollowBackUserUseCase,
    private val unFollowUserUseCase: UnFollowUserUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unLikedPostUseCase: UnLikePostUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(){

    private val _uiState = MutableStateFlow(Profile2UiState())
    val uiState = _uiState.asStateFlow()

    private val userId = savedStateHandle.get<String>(Constants.USER_ID_KEY)

    init {
        getUser()
    }

    fun onEvent( event: Profile2UiEvents ) {
        when(event){
            Profile2UiEvents.FollowButtonClicked -> {
                _uiState.update {
                    it.copy(
                        followingUnfollowingUserInProgress = true,
                        error = null
                    )
                }
                when {
                    _uiState.value.friends -> {
                        _uiState.update {
                            it.copy(
                                friends = false,
                                notFriends = false,
                                followBackUser = true,
                                followedUser = false
                            )
                        }
                        unFollowUser()
                    }
                    _uiState.value.notFriends -> {
                        _uiState.update {
                            it.copy(
                                friends = false,
                                notFriends = false,
                                followBackUser = false,
                                followedUser = true
                            )
                        }
                        followUser()
                    }
                    _uiState.value.followBackUser -> {
                        _uiState.update {
                            it.copy(
                                friends = true,
                                notFriends = false,
                                followBackUser = false,
                                followedUser = false
                            )
                        }
                        followBackUser()
                    }
                    _uiState.value.followedUser -> {
                        _uiState.update {
                            it.copy(
                                friends = false,
                                notFriends = true,
                                followBackUser = false,
                                followedUser = false
                            )
                        }
                        unFollowUser()
                    }
                }

                Log.v("PInit", "Following user: ${uiState.value.followedUser}\nFollow back user: ${uiState.value.followBackUser}\nFriends: ${uiState.value.friends}\nNot Friends: ${uiState.value.notFriends}")
            }

            Profile2UiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        gettingUserPostError = null,
                        isGettingUserPosts = true,
                        userPosts = mutableListOf()
                    )
                }
                getUserPosts()
            }
            is Profile2UiEvents.PostLiked -> {
                likePost(event.postId, event.postOwnerId)
            }
            is Profile2UiEvents.PostUnLiked -> {
                unLikePost(event.postId)
            }
        }
    }

    private fun getUser() = viewModelScope.launch {
        userId?.let {
            getUserByIdUseCase.invoke(it).collectLatest { apiResponse ->
                when(apiResponse){
                    is Response.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = apiResponse.exception
                            )
                        }
                    }
                    is Response.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                otherUser = apiResponse.data
                            )
                        }
                        getUserPosts()
                    }
                    Response.Undefined -> Unit
                }
            }
        }
    }

    fun initializeState( currentUser: User) {
        _uiState.value.otherUser?.let { otherUser ->
            _uiState.update {
                it.copy(
                    friends = currentUser.id in otherUser.followers && otherUser.id in currentUser.followers, //friends
                    followedUser = currentUser.id in otherUser.followers && otherUser.id !in currentUser.followers, //following
                    followBackUser = otherUser.id in currentUser.followers && currentUser.id !in otherUser.followers, //follow back
                    notFriends = currentUser.id !in otherUser.followers && otherUser.id !in currentUser.followers,  //follow
                    currentUser = currentUser,
                    otherUser = otherUser
                )
            }
        }
    }

    private fun followUser() = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null && _uiState.value.otherUser != null ) {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.Follow.name,
                fromUserProfileImageUrl = _uiState.value.currentUser!!.profileImageUrl,
                fromUserName = "${_uiState.value.currentUser!!.firstName} ${_uiState.value.currentUser!!.lastName}",
                fromUserId = _uiState.value.currentUser!!.id,
                postedAt = System.currentTimeMillis(),
                toUserId = _uiState.value.otherUser!!.id
            )

            _uiState.update {
                it.copy(
                    createdNotificationId = notification.id
                )
            }

            val task = followUserUseCase.invoke(  _uiState.value.currentUser!!.id, _uiState.value.otherUser!!.id, notification  )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = task.exception,
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    error = "Something went wrong!!"
                )
            }
        }

    }

    private fun unFollowUser() = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null && _uiState.value.otherUser != null ) {
            val task = unFollowUserUseCase.invoke(  _uiState.value.currentUser!!.id, _uiState.value.otherUser!!.id, _uiState.value.createdNotificationId  )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = task.exception,
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    error = "Something went wrong!!"
                )
            }
        }
    }

    private fun followBackUser() = viewModelScope.launch (Dispatchers.IO){
        if ( _uiState.value.currentUser != null && _uiState.value.otherUser != null ) {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                fromUserProfileImageUrl = _uiState.value.currentUser!!.profileImageUrl,
                type = NotificationType.FollowBack.name,
                fromUserName = "${_uiState.value.currentUser!!.firstName} ${_uiState.value.currentUser!!.lastName}",
                fromUserId = _uiState.value.currentUser!!.id,
                postedAt = System.currentTimeMillis(),
                toUserId = _uiState.value.otherUser!!.id
            )

            _uiState.update {
                it.copy(
                    createdNotificationId = notification.id
                )
            }

            val task = followBackUserUseCase.invoke(  _uiState.value.currentUser!!.id, _uiState.value.otherUser!!.id, notification  )

            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = task.exception,
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            followingUnfollowingUserInProgress = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }

        } else {
            _uiState.update {
                it.copy(
                    error = "Something went wrong!!"
                )
            }
        }

    }


    private fun getUserPosts() = viewModelScope.launch {
        if ( userId != null ) {
            getUserPostsUseCase.invoke(userId).collectLatest { apiResponse ->
                when(apiResponse){
                    is Response.Failure -> {
                        _uiState.update {
                            it.copy(
                                isGettingUserPosts = false,
                                gettingUserPostError = apiResponse.exception
                            )
                        }
                    }
                    is Response.Success -> {
                        _uiState.update {
                            it.copy(
                                isGettingUserPosts = false,
                                userPosts = apiResponse.data
                            )
                        }
                    }
                    Response.Undefined -> Unit
                }
            }
        }
    }

    private fun likePost(postId: String, postOwnerId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.currentUser?.let {
            if (  it.id != postOwnerId ) {
                val notification = Notification(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.Like.name,
                    fromUserName = "${it.firstName} ${it.lastName}",
                    fromUserProfileImageUrl = it.profileImageUrl,
                    fromUserId = it.id,
                    postId = postId,
                    postedAt = System.currentTimeMillis(),
                    toUserId = postOwnerId
                )
                likePostUseCase.invoke(it.id, postId, notification)
            } else {
                likePostUseCase.invoke(it.id, postId, null)
            }
        }
    }

    private fun unLikePost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.currentUser?.let {
            unLikedPostUseCase.invoke(it.id, postId, null)
        }
    }

}