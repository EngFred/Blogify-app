package com.engineerfred.kotlin.next.domain.repository

import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface AiChatCacheRepository {
    suspend fun addAiChat(aiChat: AiChat) : Response<Any>
    suspend fun deleteAiChat(aiChatId: String) : Response<Any>
    fun getAllAiChatsInCache() : Flow<Response<List<AiChat>>>
    suspend fun deleteAllChatsWithAi() : Response<Any>
}