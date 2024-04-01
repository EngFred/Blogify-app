package com.engineerfred.kotlin.next.domain.usecases.gemini

import com.engineerfred.kotlin.next.domain.repository.AiChatCacheRepository
import javax.inject.Inject

class DeleteAiChatFromCacheUseCase @Inject constructor(
    private val cacheRepository: AiChatCacheRepository
) {
    suspend operator fun invoke( aiChatId: String ) = cacheRepository.deleteAiChat(aiChatId)
}