package com.engineerfred.kotlin.next.presentation.screens.profile

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.User

data class ProfileUiState(
    val profileImageUrl: String = "",
    val coverImageUrl: String = "",
    val updatingCoverPhoto: Boolean = false,
    val updatingProfilePhoto: Boolean = false,

    val profileImageUpdateError: String? = null,
    val coverImageUpdateError: String? = null,

    val userPosts: MutableList<Post> = mutableListOf(),
    val isGettingUserPosts: Boolean = true,
    val gettingUserPostError: String? = null,

    val currentUser: User? = null
)
