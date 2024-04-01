package com.engineerfred.kotlin.next.domain.model

data class Comment(
    val id: String,
    val content: String,
    val userId: String,
    val userName: String,
    val userProfileImageUrl: String?,
    val likesCount: Long = 0L,
    val timestamp: Long,
    val postId: String,
    val likedBy: ArrayList<String>? = ArrayList(),
    val repliesCount: Long = 0L
) {
    constructor() : this(
        id = "",
        content = "",
        userId = "",
        userProfileImageUrl = null,
        userName = "",
        timestamp = 0L,
        postId = "",
    )
}
