package com.engineerfred.kotlin.next.presentation.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.posts.GetUserPostsUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.LikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.UnLikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.UpdateCoverImageUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.UpdateProfileImageUseCase
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
class ProfileViewModel @Inject constructor(
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val updateCoverImageUseCase: UpdateCoverImageUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unLikedPostUseCase: UnLikePostUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        getUserPosts()
    }

    fun onEvent( event: ProfileUiEvents ) {
        when(event){
            is ProfileUiEvents.CoverImageUrlChanged -> {
                _uiState.update {
                    it.copy(
                        coverImageUrl = event.coverImageUrl,
                        updatingCoverPhoto = true
                    )
                }
                updateCoverImage(event.context, event.coverImageUrl)
            }
            is ProfileUiEvents.ProfileImageUrlChanged -> {
                _uiState.update {
                    it.copy(
                        profileImageUrl = event.profileImageUrl,
                        updatingProfilePhoto = true
                    )
                }
                updateProfileImage(event.context, event.profileImageUrl)
            }

            ProfileUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        gettingUserPostError = null,
                        isGettingUserPosts = true,
                        userPosts = mutableListOf()
                    )
                }
                getUserPosts()
            }

            is ProfileUiEvents.PostLiked -> {
                likePost(event.postId)
            }
            is ProfileUiEvents.PostUnLiked -> {
                unLikePost(event.postId)
            }
        }
    }

    private fun updateProfileImage( context: Context, imageUrl: String) = viewModelScope.launch( Dispatchers.IO ) {
        loggedInUserId?.let {
            val task = updateProfileImageUseCase.invoke( it, imageUrl, context )
            when(task){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            updatingProfilePhoto = false,
                            profileImageUpdateError = task.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            updatingProfilePhoto = false
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun updateCoverImage( context: Context, imageUrl: String) = viewModelScope.launch( Dispatchers.IO ) {
        loggedInUserId?.let {
            val task = updateCoverImageUseCase.invoke( it, imageUrl, context )
            when(task){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            updatingCoverPhoto = false,
                            coverImageUpdateError = task.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            updatingCoverPhoto = false,
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun getUserPosts() = viewModelScope.launch {
        if ( loggedInUserId != null ) {
            getUserPostsUseCase.invoke(loggedInUserId).collectLatest { apiResponse ->
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

    private fun likePost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.currentUser?.let {
            likePostUseCase.invoke(it.id, postId, null)
        }
    }

    private fun unLikePost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.currentUser?.let {
            unLikedPostUseCase.invoke(it.id, postId, null)
        }
    }

    fun setCurrentUser(currentUser: User) {
        _uiState.update {
            it.copy(
                currentUser = currentUser
            )
        }
    }

}