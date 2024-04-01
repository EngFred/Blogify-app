package com.engineerfred.kotlin.next.presentation.screens.tagFriends

data class TagFriendsUiState(
    val isSearching: Boolean = false,
    val searchName: String = "",
    val taggedFriendsCount: Int = 0,
    val fixed: Boolean = true,
    val taggedFriends: ArrayList<String>  = ArrayList()
)
