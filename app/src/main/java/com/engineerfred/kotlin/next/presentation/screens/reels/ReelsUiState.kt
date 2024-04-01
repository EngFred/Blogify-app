package com.engineerfred.kotlin.next.presentation.screens.reels

import com.engineerfred.kotlin.next.domain.model.Post

data class ReelsUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val reels: List<Post> = emptyList()
)