package com.engineerfred.kotlin.next.presentation.screens.create

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.PostType
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.usecases.posts.AddPostUseCase
import com.engineerfred.kotlin.next.domain.usecases.posts.DeletePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val addPostUseCase: AddPostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private var _uiState  = MutableStateFlow(CreateUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent( event: CreateUiEvents ) {
        _uiState.update {
            it.copy(
                invalidText = null
            )
        }
        when (event) {
            is CreateUiEvents.ImageUrlChanged -> {
                val newImageUrl = event.url
                if ( newImageUrl !in _uiState.value.imagesCollection ) {
                    _uiState.value.imagesCollection.add(event.url)
                    _uiState.update {
                        it.copy(
                            imagesMutableList = it.imagesMutableList.toMutableList().apply { add(event.url) }
                        )
                    }
                }
            }

            is CreateUiEvents.PostTextValueChanged -> {
                _uiState.update {
                    it.copy(
                        captionTextValue = event.text
                    )
                }
            }

            is CreateUiEvents.PostTypeChanged -> {
                _uiState.update {
                    it.copy(
                        postType = event.postType,
                        videoUrl = null,
                        imagesCollection = ArrayList(),
                        imagesMutableList = mutableListOf()
                    )
                }
                Log.d("#2", "Post type: ${_uiState.value.postType}")
            }

            is CreateUiEvents.FeelingSelected -> {
                _uiState.update {
                    it.copy(
                        feeling = event.feeling
                    )
                }
            }
            is CreateUiEvents.LocationSelected -> {
                _uiState.update {
                    it.copy(
                        location = event.location
                    )
                }
            }

            is CreateUiEvents.FriendsTagged ->  {
                _uiState.update {
                    it.copy(
                        taggedFriends = event.friendsTagged
                    )
                }
            }

            is CreateUiEvents.PostButtonClicked -> {
                if ( _uiState.value.postType == PostType.Post.name ) {
                    if ( _uiState.value.imagesCollection.isNotEmpty() || _uiState.value.captionTextValue.isNotEmpty() ) {
                        _uiState.update {
                            it.copy(
                                addingPostInProgress = true
                            )
                        }
                        addPost( _uiState.value.post, event.context )
                    }else {
                        _uiState.update {
                            it.copy(
                                invalidText = "Missing some information"
                            )
                        }
                    }
                } else {
                    if ( !_uiState.value.videoUrl.isNullOrEmpty() ) {
                        _uiState.update {
                            it.copy(
                                addingPostInProgress = true
                            )
                        }
                        addPost( _uiState.value.post, event.context )
                    } else {
                        _uiState.update {
                            it.copy(
                                invalidText = "Choose a video!"
                            )
                        }
                    }
                }
            }

            is CreateUiEvents.VideoUrlChanged -> {
                _uiState.update {
                    it.copy(
                        videoUrl = event.url
                    )
                }
            }
        }
    }

    private fun addPost(  post: Post, context: Context ) = viewModelScope.launch( Dispatchers.IO ) {
        addPostUseCase.invoke(post, context)
    }

    fun setCurrentUser(currentUser: User) {
        _uiState.update {
            it.copy(
                currentUser = currentUser
            )
        }
    }
}