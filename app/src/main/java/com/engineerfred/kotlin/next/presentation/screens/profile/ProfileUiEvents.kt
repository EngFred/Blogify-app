package com.engineerfred.kotlin.next.presentation.screens.profile

import android.content.Context

sealed class ProfileUiEvents {
    data class ProfileImageUrlChanged( val profileImageUrl: String, val context: Context ) : ProfileUiEvents()
    data class CoverImageUrlChanged( val coverImageUrl: String, val context: Context  ) : ProfileUiEvents()
    data object RetryClicked: ProfileUiEvents()
    data class PostLiked( val postId: String ) : ProfileUiEvents()
    data class PostUnLiked( val postId: String) : ProfileUiEvents()
}