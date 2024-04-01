package com.engineerfred.kotlin.next.presentation.screens.chat

import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.domain.model.User

data class ChatUiState(
    val isGettingReceiver: Boolean = true,
    val userNotFound: Boolean = false,
    val gettingChatMessages: Boolean = true,
    val loadError: String? = null,
    val gettingChatMessagesError: String? = null,
    val sendingChatMessageError: String? = null,
    val gettingReceiverError: String? = null,
    val chatMessages: List<ChatMessage> = emptyList(),

    val receiverId: String = "",
    val receiver: User? = null,

    val currentUser: User? = null,

    val selectedMediaType: String? = null,
    val messageContent: String = "",
    val mediaUrl: String? = null,

    val sendingMessage: Boolean = false
)