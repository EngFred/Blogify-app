package com.engineerfred.kotlin.next.presentation.screens.people

sealed class PeopleUiEvents {
    data object RetryClicked: PeopleUiEvents()
    data class FollowedUser(val userId: String) : PeopleUiEvents()
    data class UnFollowedUser(val userId: String) : PeopleUiEvents()
    data class FollowedBackUser(val userId: String) : PeopleUiEvents()
}