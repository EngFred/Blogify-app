package com.engineerfred.kotlin.next.presentation.screens.create

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.PostAudience
import com.engineerfred.kotlin.next.domain.model.PostType
import com.engineerfred.kotlin.next.domain.model.User
import java.util.UUID

data class CreateUiState(
    val feeling: String? = null,
    val location: String? = null,
    val captionTextValue: String = "",
    val videoUrl: String? = null,
    val imagesCollection: ArrayList<String> = ArrayList(),
    val imagesMutableList: MutableList<String> = mutableListOf(),
    val postType: String = PostType.Post.name,
    val taggedFriends: ArrayList<String> = ArrayList(),
    val invalidText: String? = null,
    val currentUserProfileImageUrl: String? = null,
    val postAudience: String =  PostAudience.Public.name,

    val addPostError: String? = null,
    val addingPostInProgress: Boolean = false,

    val currentUser: User? = null,

    val uploadingPost: Boolean = false
) {

    val post = Post(
        id = UUID.randomUUID().toString(),
        type = postType,
        caption = captionTextValue,
        videoUrl = videoUrl,
        imagesCollection = imagesCollection,
        ownerId = currentUser?.id ?: "null",
        ownerName = "${currentUser?.firstName} ${currentUser?.lastName}",
        ownerProfileImageUrl = currentUser?.profileImageUrl,
        audience = postAudience,
        likedBy = ArrayList(),
        commentsCount = 0L,
        sharesCount = 0L,
        friendsTagged = taggedFriends,
        feeling = feeling,
        location = location,
        postedOn = System.currentTimeMillis()
    )

}