package com.engineerfred.kotlin.next.presentation.screens.tagFriends

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TagFriendsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagFriendsUiState())
    val uiState = _uiState.asStateFlow()

    init {

    }

    fun onEvent( event: TagFriendsUIEvents ) {
        when( event ) {
            is TagFriendsUIEvents.NameChanged -> {
                _uiState.update {
                    it.copy(
                        searchName = event.name
                    )
                }
            }
            TagFriendsUIEvents.SearchClicked -> {
                //Todo, search for someone in database
            }

            is TagFriendsUIEvents.FriendAdded -> {

                _uiState.update {
                    it.copy(
                        taggedFriendsCount = it.taggedFriendsCount+1
                    )
                }

                if ( event.friend !in _uiState.value.taggedFriends )
                    _uiState.value.taggedFriends.add( event.friend )
                else _uiState.value.taggedFriends.remove( event.friend )

                Log.d("#", "Tagged Friends: ${_uiState.value.taggedFriends}")

            }
        }
    }

}