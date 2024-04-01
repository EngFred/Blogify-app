package com.engineerfred.kotlin.next.domain.model

data class Post(
    val id: String,
    val type: String,
    val caption: String,
    val videoUrl: String? = null,
    val imagesCollection: ArrayList<String> = ArrayList(),
    val ownerId: String,
    val ownerName: String,
    val ownerProfileImageUrl: String?,
    val audience: String,
    val likedBy: ArrayList<String> = ArrayList(),
    val commentsCount: Long = 0L,
    val likesCount: Long = 0L,
    val sharesCount: Long = 0L,
    val friendsTagged: ArrayList<String>,
    val feeling: String?,
    val location: String?,
    val postedOn: Long,
) {
    constructor(): this(
        id = "",
        type = PostType.Post.name,
        caption = "",
        videoUrl = null,
        ownerId = "",
        ownerName = "",
        ownerProfileImageUrl = "",
        audience = PostAudience.Public.name,
        friendsTagged = ArrayList(),
        feeling = "",
        location = "",
        postedOn = 0L
    )

}

enum class PostType {
    Post,
    Reel
}

enum class PostAudience {
    Public,
    Friends,
    Followers
}
