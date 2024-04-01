package com.engineerfred.kotlin.next.domain.usecases.gemini

import com.engineerfred.kotlin.next.domain.repository.AiChatCacheRepository
import javax.inject.Inject

class GetAllAiChatFromCacheUseCase @Inject constructor(
    private val cacheRepository: AiChatCacheRepository
) {
    operator fun invoke() = cacheRepository.getAllAiChatsInCache()
}