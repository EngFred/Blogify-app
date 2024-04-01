package com.engineerfred.kotlin.next.presentation.screens.chatWithGemini

import android.content.Context

sealed class ChatWithGeminiUiEvents {
    data class SendMessage( val context: Context ) : ChatWithGeminiUiEvents()
    data class ImageUrlChanged(val newImageUrl: String): ChatWithGeminiUiEvents()
    data class MessageContentChanged( val message: String ): ChatWithGeminiUiEvents()
    data object ClearChat: ChatWithGeminiUiEvents()
}
