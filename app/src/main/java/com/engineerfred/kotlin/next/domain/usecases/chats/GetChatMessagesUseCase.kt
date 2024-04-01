package com.engineerfred.kotlin.next.domain.usecases.chats

import com.engineerfred.kotlin.next.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke( currentUserId: String, receiverUserId: String ) = chatRepository.getChatMessages( currentUserId, receiverUserId )
}