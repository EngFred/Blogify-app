package com.engineerfred.kotlin.next.domain.model

data class User(
    val id: String,
    val lastName: String,
    val firstName: String,
    val email: String,
    val isVerified: Boolean = false,
    val profileImageUrl: String? = null,
    val coverImageUrl: String? = null,
    val about: String? = null,
    val following: ArrayList<String> = ArrayList(),
    val followingCount: Long = 0L,
    val followers: ArrayList<String> = ArrayList(),
    val followersCount: Long = 0L,
    val joinDate: Long
){
    constructor() : this(
        id = "",
        lastName = "",
        firstName = "",
        email = "",
        joinDate = 0L
    )
}
