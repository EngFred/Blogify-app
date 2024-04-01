package com.engineerfred.kotlin.next.data.remote

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.domain.repository.GeminiRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiRepositoryImpl : GeminiRepository {

    override suspend fun getResponse(userPrompt: String): Response<AiChat> {
        return try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = Constants.googleApiKey
            )

            val response = generativeModel.generateContent(userPrompt)
            val chat = AiChat(
                prompt = response.text ?: "error",
                imageUrl = null,
                isFromUser = false
            )
            Response.Success(chat)
        }catch (ex: Exception) {
            Log.d("Gemini", "$ex")
            Response.Failure("$ex")
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun getResponse(userPrompt: String, imageUrl: String, context: Context ): Response<AiChat> {
        return try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro-vision",
                apiKey = Constants.googleApiKey
            )

            val contentResolver = context.contentResolver
            val imageUri = Uri.parse(imageUrl)
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val inputContent = content {
                image(bitmap)
                text(userPrompt)
            }

            val response = generativeModel.generateContent(inputContent)
            val chat = AiChat(
                prompt = response.text ?: "error",
                imageUrl = null,
                isFromUser = false
            )
            Response.Success(chat)
        } catch (ex: Exception) {
            Log.d("Gemini", "$ex")
            Response.Failure("$ex")
        }
    }
}