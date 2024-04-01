package com.engineerfred.kotlin.next.presentation.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.engineerfred.kotlin.next.domain.model.User

class CommonViewModel : ViewModel() {

    var friendsList = ArrayList<String>()
        private set

    var currentUser: User? = null
    var theme = mutableStateOf(false)

    fun addList( list: ArrayList<String> ) {
        friendsList = list
    }

    fun addUser( user: User ) {
        currentUser = user
    }

    fun changeTheme(isDarkTheme: Boolean) {
        theme.value = isDarkTheme
    }

}