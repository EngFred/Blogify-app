package com.engineerfred.kotlin.next.domain.repository

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.utils.Response

interface GeminiRepository {
    suspend fun getResponse( userPrompt: String ) : Response<AiChat>
    suspend fun getResponse( userPrompt: String, imageUrl: String, context: Context) : Response<AiChat>
}