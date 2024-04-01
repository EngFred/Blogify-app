package com.engineerfred.kotlin.next.utils

import androidx.datastore.preferences.core.booleanPreferencesKey

object Constants {
    const val POSTS_COLLECTION = "Posts"
    const val REELS_COLLECTION = "Reels"
    const val USERS_COLLECTION = "Users"
    const val MESSAGES_COLLECTION = "Messages"
    const val CHATS_COLLECTION = "Chats"
    const val NOTIFICATIONS_COLLECTION = "Notifications"
    const val COMMENTS_COLLECTION = "Comments"
    const val REPLIES_THREAD_COLLECTION = "RepliesThread"

    const val VIDEOS_STORAGE = "Videos"
    const val POST_IMAGES_STORAGE = "Images"
    const val PROFILE_IMAGES_STORAGE = "ProfileImages"
    const val COVER_IMAGES_STORAGE = "CoverImages"
    const val CHAT_IMAGES_STORAGE = "ChatImages"
    const val CHAT_VIDEOS_STORAGE = "ChatVideos"
    const val FEELING_KEY = "feeling"
    const val ADDING_POST_KEY = "addingPost"
    const val LOCATION_KEY = "location"
    const val POST_ID_KEY = "postId"
    const val VIDEO_URL_KEY = "videoUrl"
    const val IMAGE_URL_KEY = "imageUrl"
    const val USER_ID_KEY = "userId"
    const val FIRST_NAME_KEY = "firstName"
    const val LAST_NAME_KEY = "lastName"
    const val USER_PASSWORD_KEY = "password"
    const val EMAIL_KEY = "email"
    const val TAGGED_FRIENDS_KEY = "taggedFriends"
    const val COMMENT_ID_KEY = "commentId"
    const val POST_OWNER_NAME_KEY = "postOwnerName"
    const val COMMENT_OWNER_NAME_KEY = "commentOwnerName"
    const val COMMENT_OWNER_ID_KEY = "commentOwnerId"


    val USER_LOGIN_INFO_KEY = booleanPreferencesKey("userLoginInfo")
    val THEME_KEY = booleanPreferencesKey("theme")

    val googleApiKey = "AIzaSyAz6prWl8-o5_jPXVJhey3xlTz8zn4_yCE"
}