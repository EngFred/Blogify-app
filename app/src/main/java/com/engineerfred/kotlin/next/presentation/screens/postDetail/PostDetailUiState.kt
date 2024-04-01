package com.engineerfred.kotlin.next.presentation.screens.postDetail

import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.User

data class PostDetailUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val post: Post? = null,

    val inputTextValue: String = "",
    val isReplying: Boolean = false,

    val postComments: List<Comment> = emptyList(),
    val loadingPostComments: Boolean = true,
    val postCommentsLoadError: String? = null,

    val currentUser: User? = null,

    val generatedNotificationId: String? = null

)
