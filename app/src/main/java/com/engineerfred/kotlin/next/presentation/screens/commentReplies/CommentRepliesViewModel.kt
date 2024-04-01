package com.engineerfred.kotlin.next.presentation.screens.commentReplies

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.comments.GetCommentByIdUseCase
import com.engineerfred.kotlin.next.domain.usecases.comments.GetCommentRepliesUseCase
import com.engineerfred.kotlin.next.domain.usecases.comments.PostReplyUseCase
import com.engineerfred.kotlin.next.utils.Constants
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
class CommentRepliesViewModel @Inject constructor(
    private val getCommentRepliesUseCase: GetCommentRepliesUseCase,
    private val postReplyUseCase: PostReplyUseCase,
    private val getCommentByIdUseCase: GetCommentByIdUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val commentId = savedStateHandle.get<String>(Constants.COMMENT_ID_KEY)
    private val postId = savedStateHandle.get<String>(Constants.POST_ID_KEY)
    private val postOwnerName = savedStateHandle.get<String>(Constants.POST_OWNER_NAME_KEY)
    private val commentOwnerName = savedStateHandle.get<String>(Constants.COMMENT_OWNER_NAME_KEY)
    private val commentOwnerId = savedStateHandle.get<String>(Constants.COMMENT_OWNER_ID_KEY)

    private val _uiState = MutableStateFlow(CommentRepliesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if ( !commentOwnerName.isNullOrEmpty() && !commentOwnerId.isNullOrEmpty() ) {
            _uiState.update {
                it.copy(
                    commentOwnerName = commentOwnerName,
                    commentOwnerId = commentOwnerId
                )
            }
        }
        commentId?.let {
            getCommentById(it)
        }
        Log.v("Boo", "CommentId: $commentId\nPostId: $postId\nPostOwnerName: $postOwnerName\nCommentOwnerName: $commentOwnerName\nCommentOwnerId: $commentOwnerId")
    }

    fun onEvent( event: CommentRepliesUiEvents ) {
        when(event){
            CommentRepliesUiEvents.ReplyButtonClicked -> {
                if (
                    _uiState.value.replyTextValue.isNotEmpty() &&
                    !_uiState.value.commentOwnerName.isNullOrEmpty() &&
                    !_uiState.value.commentOwnerId.isNullOrEmpty() &&
                    !postOwnerName.isNullOrEmpty() && !commentId.isNullOrEmpty()
                ) {
                    postReply(commentId, _uiState.value.commentOwnerName!!, _uiState.value.commentOwnerId!!, postOwnerName )
                } else {
                    Log.i("Boo", "Some required information is either null or empty or the text input value is empty!!")
                }
            }
            is CommentRepliesUiEvents.ReplyTextChanged -> {
                _uiState.update {
                    it.copy(
                        replyTextValue = event.replyText
                    )
                }
            }
            is CommentRepliesUiEvents.ReplyTo -> {
                _uiState.update {
                    it.copy(
                        commentOwnerName = event.commentOwnerName,
                        commentOwnerId = event.commentOwnerId,
                        replyingTo = event.commentOwnerName
                    )
                }
            }

            CommentRepliesUiEvents.RetryLoadingCommentRepliesButtonClicked -> {
                _uiState.update {
                    it.copy(
                        loadingCommentReplies = true,
                        commentRepliesLoadError = null,
                        commentReplies = emptyList()
                    )
                }
                getCommentReplies(commentId!!)
            }

            CommentRepliesUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        loadError = null,
                        isLoading = false,
                        comment = null
                    )
                }
                commentId?.let {
                    getCommentById(commentId)
                }
            }
        }
    }

    private fun postReply(
        commentId: String,
        commentOwnerName: String,
        commentOwnerId: String,
        parentPostOwnerName: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        if ( _uiState.value.currentUser != null ) {
            val currentUser = _uiState.value.currentUser!!
            val replyText = _uiState.value.replyTextValue
            val reply = Reply(
                id = UUID.randomUUID().toString(),
                content = replyText,
                userId = currentUser.id,
                userName = "${currentUser.firstName} ${currentUser.lastName}",
                userProfileImageUrl = currentUser.profileImageUrl,
                timestamp = System.currentTimeMillis(),
                commentId = commentId,
                replyingToName = commentOwnerName,
                replyingToId = commentOwnerId
            )

            if ( currentUser.id != commentOwnerId ) {
                Log.v("Boo", "The person replying is not the owner of the comment, so a notification is created!!! ")
                val notification = Notification(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.ReplyToComment.name,
                    fromUserName = "${currentUser.firstName} ${currentUser.lastName}",
                    fromUserProfileImageUrl = currentUser.profileImageUrl,
                    postOwnerName = parentPostOwnerName, //this won't change for a post
                    postOwnerId = currentUser.id,
                    toUserId = commentOwnerId,
                    postedAt = System.currentTimeMillis(),
                    postId = commentId
                )
                _uiState.update {
                    it.copy(
                        replyTextValue = ""
                    )
                }
                postReplyUseCase.invoke( reply, commentId, notification )
            } else {
                Log.v("Boo", "The person replying is the owner of the comment, so a notification is NOT created!!! ")
                _uiState.update {
                    it.copy(
                        replyTextValue = ""
                    )
                }
                postReplyUseCase.invoke( reply, commentId, null )
            }
        } else {
            Log.v("Boo", "The current user is null!!! ")
        }
    }

    private fun getCommentReplies(commentId: String) = viewModelScope.launch {
        getCommentRepliesUseCase.invoke(commentId).collectLatest { apiResponse ->
            when( apiResponse ) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            loadingCommentReplies = false,
                            commentRepliesLoadError =  apiResponse.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            loadingCommentReplies = false,
                            commentReplies = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun getCommentById( commentId: String ) = viewModelScope.launch {
        getCommentByIdUseCase.invoke( commentId ).collectLatest { apiResponse ->
            when(apiResponse) {
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
                            comment = apiResponse.data
                        )
                    }
                    getCommentReplies(commentId)
                }
                Response.Undefined -> Unit
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
}