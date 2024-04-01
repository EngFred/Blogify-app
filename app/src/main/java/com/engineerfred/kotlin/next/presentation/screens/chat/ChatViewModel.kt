package com.engineerfred.kotlin.next.presentation.screens.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.domain.model.MessageType
import com.engineerfred.kotlin.next.domain.usecases.chats.GetChatMessagesUseCase
import com.engineerfred.kotlin.next.domain.usecases.chats.SendMessageUseCase
import com.engineerfred.kotlin.next.domain.usecases.users.GetUserByIdUseCase
import com.engineerfred.kotlin.next.utils.Constants
import com.engineerfred.kotlin.next.utils.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val receiverId = savedStateHandle.get<String>(Constants.USER_ID_KEY)


    init {
        receiverId?.let {
            getReceiver(it)
        }
    }

    fun onEvent( event: ChatUiEvents ) {
        _uiState.update {
            it.copy(
                sendingChatMessageError = null
            )
        }
        when(event) {
            is ChatUiEvents.MediaUrlChanged -> {
                _uiState.update {
                    it.copy(
                        mediaUrl = event.mediaUrl,
                        selectedMediaType = event.mediaType
                    )
                }
            }
            is ChatUiEvents.MessageContentChanged -> {
                _uiState.update {
                    it.copy(
                        messageContent = event.message
                    )
                }
            }
            is ChatUiEvents.SendMessage -> {

                val messageType = when {
                    uiState.value.messageContent.isEmpty() -> {
                        when {
                            !uiState.value.mediaUrl.isNullOrEmpty() -> {
                                when (uiState.value.selectedMediaType) {
                                    "photo" -> MessageType.ImageOnly.name
                                    "video" -> MessageType.VideoOnly.name
                                    else -> MessageType.TextOnly.name
                                }
                            }
                            else -> MessageType.ImageOnly.name
                        }
                    }

                    else -> {
                        when {
                            !uiState.value.mediaUrl.isNullOrEmpty() -> {
                                when (uiState.value.selectedMediaType) {
                                    "photo" -> MessageType.ImageWithCaption.name
                                    "video" -> MessageType.VideoWithCaption.name
                                    else -> MessageType.TextOnly.name
                                }
                            }
                            else -> MessageType.TextOnly.name
                        }
                    }
                }

                val messageContent = _uiState.value.messageContent
                val mediaUrl = _uiState.value.mediaUrl
                val localVideoUri = if ( _uiState.value.selectedMediaType == "video") _uiState.value.mediaUrl else null

                val chatMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = FirebaseAuth.getInstance().uid ?: "",
                    receiverId = receiverId ?: "",
                    type = messageType,
                    content = messageContent,
                    mediaUrl = mediaUrl,
                    localVideoUri = localVideoUri,
                    timeStamp = System.currentTimeMillis()
                )

                if ( _uiState.value.messageContent.isNotEmpty() || !_uiState.value.mediaUrl.isNullOrEmpty() ) {
                    Log.i("BOO0M", "Message $chatMessage")
                    _uiState.update {
                        it.copy(
                            messageContent = "",
                            mediaUrl = null,
                            selectedMediaType = null
                        )
                    }
                    sendMessage(chatMessage, event.context)
                }

            }
        }
    }

    private fun getReceiver(receiverId: String ) = viewModelScope.launch {
        getUserByIdUseCase.invoke(receiverId).collectLatest { apiResponse ->
            when(apiResponse) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            gettingReceiverError = apiResponse.exception,
                            isGettingReceiver = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            isGettingReceiver = false,
                            receiver = apiResponse.data
                        )
                    }
                    getChatMessages( FirebaseAuth.getInstance().uid!!, receiverId )
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun sendMessage(chatMessage: ChatMessage, context: Context) = viewModelScope.launch( Dispatchers.IO) {
        val task = sendChatMessageUseCase.invoke( chatMessage, context )
        when(task) {
            is Response.Failure -> {
                _uiState.update {
                    it.copy(
                        sendingChatMessageError = task.exception
                    )
                }
            }
            is Response.Success -> Unit
            Response.Undefined -> Unit
        }
    }

    private fun getChatMessages( currentUserId: String, receiverId: String )  = viewModelScope.launch {
        getChatMessagesUseCase.invoke( currentUserId, receiverId ).collectLatest { apiResponse ->
            when(apiResponse){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            gettingChatMessagesError = apiResponse.exception,
                            gettingChatMessages = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            gettingChatMessages = false,
                            chatMessages = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

}