package com.engineerfred.kotlin.next.presentation.screens.chatWithGemini

import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.domain.model.User

data class ChatWithGeminiUiState(
    val currentUser: User? = null,
    val isGettingResponse: Boolean = false,
    val prompt: String = "",
    val imageUrl: String? = null,
    val responseError: String? = null,
    val cacheError: String? = null,
    val messageAdded: Boolean = false,
    val clearingChat: Boolean = false,
    val clearingChatError: String? = null,
    val chatList: List<AiChat> = mutableListOf()
)
