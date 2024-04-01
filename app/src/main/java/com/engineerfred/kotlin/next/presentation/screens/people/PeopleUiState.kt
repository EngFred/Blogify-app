package com.engineerfred.kotlin.next.presentation.screens.people

import com.engineerfred.kotlin.next.domain.model.User

data class PeopleUiState (
    val users: MutableList<User> = mutableListOf(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val currentUser: User? = null,
    val followError: String? = null,
    val followingUserInProgress: Boolean = false
)