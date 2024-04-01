package com.engineerfred.kotlin.next.domain.repository

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendChat( message: ChatMessage, context: Context ) : Response<Any>
    fun getChatMessages( currentUserId: String, receiverId: String ) : Flow<Response<List<ChatMessage>>>
}