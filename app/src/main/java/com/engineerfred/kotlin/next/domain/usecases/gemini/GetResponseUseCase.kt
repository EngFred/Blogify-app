package com.engineerfred.kotlin.next.domain.usecases.gemini

import com.engineerfred.kotlin.next.domain.repository.GeminiRepository
import javax.inject.Inject

class GetResponseUseCase @Inject constructor(
    private val geminiRepository: GeminiRepository
) {
    suspend operator fun invoke( userPrompt: String ) = geminiRepository.getResponse(userPrompt)
}