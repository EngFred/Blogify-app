package com.engineerfred.kotlin.next.domain.usecases.gemini

import android.content.Context
import com.engineerfred.kotlin.next.domain.repository.GeminiRepository
import javax.inject.Inject

class GetResponseWithImageInputUseCase @Inject constructor(
    private val geminiRepository: GeminiRepository
) {
    suspend operator fun invoke(
        userPrompt: String,
        imageUrl: String,
        context: Context
    ) = geminiRepository.getResponse(
        userPrompt,
        imageUrl,
        context
    )

}