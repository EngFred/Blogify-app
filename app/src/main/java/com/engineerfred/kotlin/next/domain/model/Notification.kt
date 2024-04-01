package com.engineerfred.kotlin.next.domain.model

data class Notification(
    val id: String,
    val type: String,
    val fromUserName: String, //name of the person who triggered the notification
    val fromUserProfileImageUrl: String? = null,
    val toUserName: String? = null, //name of the person who receives the notification
    val fromUserId: String? = null, //incase of a follow, when notification clicked we can navigate to the profile screen of the person who triggered the notification
    val toUserId: String, //for retrieving the user notifications
    val postId: String? = null, //incase its a like or comment notification
    val postOwnerName: String? = null, //the creator of the post that they are reacting on %%when replying to someone's comment on someone else's post,
    val postOwnerId: String? = null,
    val postedAt: Long,
    val read: Boolean = false
) {
    constructor() : this(
        id = "",
        type = NotificationType.Follow.name,
        fromUserName = "",
        toUserId = "",
        postedAt = 0L
    )
}

enum class NotificationType{
    Follow,
    FollowBack,
    Like,
    Comment,
    ReplyToComment
}
