package com.engineerfred.kotlin.next.presentation.screens.profile2

sealed class Profile2UiEvents {
    data object FollowButtonClicked : Profile2UiEvents()
    data object RetryClicked: Profile2UiEvents()
    data class PostLiked( val postId: String, val postOwnerId: String  ) : Profile2UiEvents()
    data class PostUnLiked( val postId: String) : Profile2UiEvents()
}