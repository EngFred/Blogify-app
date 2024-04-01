package com.engineerfred.kotlin.next.presentation.screens.profile2

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.User

data class Profile2UiState(
    val isLoading: Boolean = true,
    val createdNotificationId: String? = null,
    val loadError: String? = null,
    val otherUser: User? = null,
    val currentUser: User? = null,
    val followedUser: Boolean = false,
    val followBackUser: Boolean = false,
    val friends: Boolean = false,
    val notFriends: Boolean = false,
    val followingUnfollowingUserInProgress: Boolean = false,

    val userPosts: MutableList<Post> = mutableListOf(),
    val isGettingUserPosts: Boolean = true,
    val gettingUserPostError: String? = null,

    val error: String? = null
)
