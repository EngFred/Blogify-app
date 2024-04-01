package com.engineerfred.kotlin.next.presentation.screens.feeds

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.User

data class FeedsUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val feeds: List<Post> = emptyList(),
    val currentUser: User? = null
)
