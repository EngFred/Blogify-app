package com.engineerfred.kotlin.next.domain.usecases.chats

import android.content.Context
import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke( chatMessage: ChatMessage, context: Context ) = chatRepository.sendChat( chatMessage, context )
}