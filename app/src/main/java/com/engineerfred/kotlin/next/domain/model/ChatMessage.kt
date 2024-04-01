package com.engineerfred.kotlin.next.domain.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val type: String,
    val content: String? = null,
    val mediaUrl: String? = null,
    val localVideoUri: String? = null,
    val timeStamp: Long = System.currentTimeMillis(),
) {

    constructor() : this(
        id = "",
        senderId = "",
        receiverId = "",
        type = MessageType.TextOnly.name
    )

}

enum class MessageType{
    TextOnly,
    ImageOnly,
    VideoOnly,
    ImageWithCaption,
    VideoWithCaption
}
