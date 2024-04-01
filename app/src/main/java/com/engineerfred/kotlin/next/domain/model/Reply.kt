package com.engineerfred.kotlin.next.domain.model

data class Reply(
    val id: String,
    val content: String,
    val userId: String,
    val userName: String,
    val userProfileImageUrl: String?,
    val likesCount: Long = 0L,
    val likedBy: ArrayList<String>? = ArrayList(),
    val timestamp: Long,
    val commentId: String,
    val replyingToName: String, //name of the person we are replying to todo, Fred to Canal
    val replyingToId: String
) {
    constructor() : this(
        id = "",
        content = "",
        userId = "",
        userProfileImageUrl = "",
        userName = "",
        timestamp = 0L,
        commentId = "",
        replyingToName = "",
        replyingToId = ""
    )
}
