package com.engineerfred.kotlin.next.presentation.screens.commentReplies

sealed class CommentRepliesUiEvents {
    data object RetryLoadingCommentRepliesButtonClicked: CommentRepliesUiEvents()
    data object RetryClicked: CommentRepliesUiEvents()
    data class ReplyTextChanged( val replyText: String ) : CommentRepliesUiEvents()
    data object ReplyButtonClicked: CommentRepliesUiEvents()
    data class ReplyTo( val commentOwnerName: String, val commentOwnerId: String): CommentRepliesUiEvents()
}