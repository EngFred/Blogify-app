package com.engineerfred.kotlin.next.presentation.screens.commentReplies

import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.domain.model.User

data class CommentRepliesUiState(
    val commentReplies: List<Reply> = emptyList(),
    val loadingCommentReplies: Boolean = true,
    val commentRepliesLoadError: String? = null,
    val currentUser: User? = null,
    val replyTextValue: String = "",

    val commentOwnerName: String? = null,
    val commentOwnerId: String? = null,

    val replyingTo: String = "",

    val isLoading: Boolean = true,
    val loadError: String? = null,
    val comment: Comment? = null
)
