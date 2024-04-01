package com.engineerfred.kotlin.next.presentation.screens.feeds

sealed class FeedsUiEvents {
    data object RetryClicked:FeedsUiEvents()
    data class PostLiked( val postId: String, val postOwnerId: String ) : FeedsUiEvents()
    data class PostUnLiked( val postId: String ) : FeedsUiEvents()
}