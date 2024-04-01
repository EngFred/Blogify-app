package com.engineerfred.kotlin.next.presentation.screens.tagFriends

sealed class TagFriendsUIEvents {
    data class NameChanged( val name: String ) : TagFriendsUIEvents()
    data class FriendAdded( val friend: String ) : TagFriendsUIEvents()
    data object SearchClicked : TagFriendsUIEvents()
}