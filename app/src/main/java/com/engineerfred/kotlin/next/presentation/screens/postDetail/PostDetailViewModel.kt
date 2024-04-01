package com.engineerfred.kotlin.next.presentation.screens.postDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.comments.GetPostCommentsUseCase
import com.engineerfred.kotlin.next.domain.usecases.comments.PostCommentUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.GetPostByIdUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.LikePostUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.UnLikePostUseCase
import com.engineerfred.kotlin.next.utils.Constants.POST_ID_KEY
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
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val likePostUseCase: LikePostUseCase,
    private val unLikedPostUseCase: UnLikePostUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val getPostCommentsUseCase: GetPostCommentsUseCase
) : ViewModel() {

    private val postId = savedStateHandle.get<String>(POST_ID_KEY)

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = PostDetailViewModel::class.java.simpleName
    }

    init {
        if ( postId != null ) {
            getPostById(postId)
            getPostComments(postId)
            Log.d(TAG, "Post ID is $postId")
        } else {
            Log.d(TAG, "Post ID is null!!!")
        }
    }

    fun onEvent( event: PostDetailEvents ) {
        when(event) {
            PostDetailEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        loadError = null,
                        post = null
                    )
                }
                if ( postId != null ) {
                    getPostById(postId)
                } else {
                    Log.d(TAG, "Post ID is null!!!")
                }
            }

            is PostDetailEvents.CommentTextChanged -> {
                _uiState.update {
                    it.copy(
                       inputTextValue = event.commentText
                    )
                }
            }

            is PostDetailEvents.PostLiked -> {
                postId?.let {
                    likePost(it, event.postOwnerId)
                }
            }
            is PostDetailEvents.PostUnLiked -> {
                postId?.let {
                    unLikePost(it)
                }
            }

            PostDetailEvents.Commented -> {
                if ( _uiState.value.inputTextValue.isNotEmpty() ) {
                    val comment = _uiState.value.inputTextValue
                    postId?.let {
                        postComment(it, _uiState.value.post!!.ownerId)
                    }
                }
            }

            PostDetailEvents.RetryLoadingPostComments ->  {
                _uiState.update {
                    it.copy(
                        loadingPostComments = true,
                        postCommentsLoadError = null,
                        postComments = emptyList()
                    )
                }
                getPostComments(postId!!)
            }
        }
    }

    private fun getPostById( postId: String )  = viewModelScope.launch {
        getPostByIdUseCase.invoke( postId ).collectLatest { apiResponse ->
            when( apiResponse ) {
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
                            post = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun likePost(postId: String, postOwnerId: String ) = viewModelScope.launch(Dispatchers.IO) {
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
                _uiState.update {
                    it.copy(
                        generatedNotificationId = notification.id
                    )
                }
                likePostUseCase.invoke(it.id, postId, notification)
            } else {
                likePostUseCase.invoke(it.id, postId, null)
            }
        }
    }

    private fun unLikePost(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.currentUser?.let {
            if ( _uiState.value.generatedNotificationId != null ) {
                unLikedPostUseCase.invoke(it.id, postId, _uiState.value.generatedNotificationId)
            } else {
                unLikedPostUseCase.invoke(it.id, postId, null)
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

    private fun postComment( postId: String, postOwnerId: String ) = viewModelScope.launch( Dispatchers.IO ) {
        _uiState.value.currentUser?.let {
            val commentText = _uiState.value.inputTextValue
            val comment = Comment(
                id = UUID.randomUUID().toString(),
                content = commentText,
                userId = it.id,
                userName = "${it.firstName} ${it.lastName}",
                userProfileImageUrl = it.profileImageUrl,
                timestamp = System.currentTimeMillis(),
                postId = postId
                )
            if ( it.id != postOwnerId ) {
                val notification = Notification(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.Comment.name,
                    fromUserName = "${it.firstName} ${it.lastName}",
                    fromUserProfileImageUrl = it.profileImageUrl,
                    toUserId = postOwnerId,
                    postedAt = System.currentTimeMillis(),
                    postId = postId
                )
                _uiState.update {
                    it.copy(
                        inputTextValue = ""
                    )
                }
                postCommentUseCase.invoke( comment, postId, notification )
            } else {
                _uiState.update {
                    it.copy(
                        inputTextValue = ""
                    )
                }
                postCommentUseCase.invoke( comment, postId, null )
            }
        }
    }

    private fun getPostComments(postId: String) = viewModelScope.launch {
        getPostCommentsUseCase.invoke(postId).collectLatest { apiResponse ->
            when( apiResponse ) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            loadingPostComments = false,
                            postCommentsLoadError = apiResponse.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            loadingPostComments = false,
                            postCommentsLoadError = null,
                            postComments = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

}