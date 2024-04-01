package com.engineerfred.kotlin.next.presentation.screens.chat

import android.content.Context

sealed class ChatUiEvents {
    data class SendMessage(val context: Context) : ChatUiEvents()
    data class MediaUrlChanged( val mediaUrl: String, val mediaType: String ): ChatUiEvents()
    data class MessageContentChanged( val message: String ): ChatUiEvents()
}