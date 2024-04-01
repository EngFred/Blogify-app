package com.engineerfred.kotlin.next.presentation.screens.feeds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.posts.GetPostsUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.LikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.UnLikePostUseCase
import com.engineerfred.kotlin.next.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unLikedPostUseCase: UnLikePostUseCase
) : ViewModel() {

    var uiState by mutableStateOf(FeedsUiState())
        private set

    init {
        getPosts()
    }

    fun onEvent( event: FeedsUiEvents ) {
        when( event ) {
            FeedsUiEvents.RetryClicked -> {
                uiState = uiState.copy(
                    isLoading = true,
                    loadError = null,
                    feeds = emptyList()
                )
                getPosts()
            }
            is FeedsUiEvents.PostLiked -> {
                likePost(event.postId, event.postOwnerId)
            }
            is FeedsUiEvents.PostUnLiked -> {
                unLikePost( event.postId )
            }
        }
    }

    private fun likePost(postId: String, postOwnerId: String) = viewModelScope.launch(Dispatchers.IO) {
        uiState.currentUser?.let {
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
        uiState.currentUser?.let {
            unLikedPostUseCase.invoke(it.id, postId, null)
        }
    }

    private fun getPosts() = viewModelScope.launch{
        getPostsUseCase.invoke().collectLatest { apiResponse ->
            when( apiResponse ) {
                is Response.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        loadError = apiResponse.exception
                    )
                }
                is Response.Success -> {
                   uiState = uiState.copy(
                       isLoading = false,
                       feeds = apiResponse.data
                   )
                }
                Response.Undefined -> Unit
            }
        }
    }

    fun setCurrentUser(currentUser: User) {
        uiState = uiState.copy(
            currentUser = currentUser
        )
    }

}