package com.engineerfred.kotlin.next.presentation.screens.postDetail

sealed class PostDetailEvents {
    data object RetryClicked: PostDetailEvents()
    data object RetryLoadingPostComments: PostDetailEvents()
    data class CommentTextChanged( val commentText: String ): PostDetailEvents()
    data class PostLiked( val postOwnerId: String ) : PostDetailEvents()
    data class PostUnLiked( val postOwnerId: String ) : PostDetailEvents()
    data object Commented : PostDetailEvents()
}