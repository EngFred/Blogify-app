package com.engineerfred.kotlin.next.domain.usecases.gemini

import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.domain.repository.AiChatCacheRepository
import javax.inject.Inject

class AddAiChatInCacheUseCase @Inject constructor(
    private val cacheRepository: AiChatCacheRepository
) {
    suspend operator fun invoke( aiChat: AiChat ) = cacheRepository.addAiChat(aiChat)
}