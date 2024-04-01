package com.engineerfred.kotlin.next.presentation.screens.chatWithGemini

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.domain.usecases.gemini.AddAiChatInCacheUseCase
import com.engineerfred.kotlin.next.domain.usecases.gemini.DeleteAiChatFromCacheUseCase
import com.engineerfred.kotlin.next.domain.usecases.gemini.DeleteAllAiChatFromCacheUseCase
import com.engineerfred.kotlin.next.domain.usecases.gemini.GetAllAiChatFromCacheUseCase
import com.engineerfred.kotlin.next.domain.usecases.gemini.GetResponseUseCase
import com.engineerfred.kotlin.next.domain.usecases.gemini.GetResponseWithImageInputUseCase
import com.engineerfred.kotlin.next.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatWithGeminiViewModel @Inject constructor(
    private val getResponseUseCase: GetResponseUseCase,
    private val getResponseWithImageInputUseCase: GetResponseWithImageInputUseCase,
    private val getAllAiChatFromCacheUseCase: GetAllAiChatFromCacheUseCase,
    private val deleteAllAiChatFromCacheUseCase: DeleteAllAiChatFromCacheUseCase,
    private val addAiChatInCacheUseCase: AddAiChatInCacheUseCase,
    private val deleteAiChatFromCacheUseCase: DeleteAiChatFromCacheUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatWithGeminiUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getChatsInCache()
    }

    fun onEvent( event: ChatWithGeminiUiEvents ) {

        if (_uiState.value.messageAdded) {
            _uiState.update {
                it.copy(
                    messageAdded = false
                )
            }
        }

        if ( _uiState.value.responseError != null ) {
            _uiState.update {
                it.copy(
                    responseError = null
                )
            }
        }

        when(event) {
            is ChatWithGeminiUiEvents.ImageUrlChanged -> {
                _uiState.update {
                    it.copy(
                        imageUrl = event.newImageUrl
                    )
                }
            }
            is ChatWithGeminiUiEvents.MessageContentChanged -> {
                _uiState.update {
                    it.copy(
                        prompt = event.message
                    )
                }
            }
            is ChatWithGeminiUiEvents.SendMessage -> {
                if ( _uiState.value.prompt.isNotEmpty() ) {
                    val userPrompt = _uiState.value.prompt
                    val imageUrl = _uiState.value.imageUrl
                    _uiState.update {
                        it.copy(
                           prompt = "",
                            imageUrl = null
                        )
                    }
                    val aiChat = AiChat(prompt = userPrompt, imageUrl = imageUrl, isFromUser = true)
                    addAiChat(aiChat, event.context)
                }
            }

            ChatWithGeminiUiEvents.ClearChat -> {
                clearChat()
            }
        }
    }

    private fun clearChat() = viewModelScope.launch( Dispatchers.IO ) {
        _uiState.update {
            it.copy(
                clearingChat = true
            )
        }
        val task = deleteAllAiChatFromCacheUseCase.invoke()
        when(task) {
            is Response.Failure -> {
                _uiState.update {
                    it.copy(
                        clearingChat = false,
                        clearingChatError = task.exception
                    )
                }
            }
            is Response.Success -> {
                _uiState.update {
                    it.copy(
                        clearingChat = false
                    )
                }
            }
            Response.Undefined -> Unit
        }
    }


    private fun addAiChat( aiChat: AiChat, context: Context ) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update {
            it.copy(
                isGettingResponse = true,
                messageAdded = false
            )
        }
        val task = addAiChatInCacheUseCase.invoke( aiChat )
        when(task){
            is Response.Failure -> {
                Log.e("ChatCache", "Error adding chat in cache...")
                _uiState.update {
                    it.copy(
                        cacheError = task.exception,
                        isGettingResponse = false
                    )
                }
            }
            is Response.Success -> {
                if ( aiChat.imageUrl.isNullOrEmpty() ) {
                    getResponse( aiChat.prompt )
                } else {
                    getResponseWithImageInput( aiChat.prompt, aiChat.imageUrl, context )
                }
                _uiState.update {
                    it.copy(
                        messageAdded = true
                    )
                }
            }
            Response.Undefined -> Unit
        }
    }

    private fun getChatsInCache() = viewModelScope.launch {
        getAllAiChatFromCacheUseCase.invoke().collectLatest { apiResponse ->
            when(apiResponse){
                is Response.Failure -> {
                    Log.e("ChatCache", "Error Getting chats in cache...")
                    _uiState.update {
                        it.copy(
                            cacheError = apiResponse.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            chatList = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    private fun getResponse( userPrompt: String ) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                messageAdded = false
            )
        }
        val task = getResponseUseCase.invoke(userPrompt)
        when(task) {
            is Response.Failure -> {
                _uiState.update {
                    it.copy(
                        isGettingResponse = false,
                        responseError = task.exception
                    )
                }
            }
            is Response.Success -> {
                _uiState.update {
                    it.copy(
                        isGettingResponse = false
                    )
                }
                addAiChatInCacheUseCase.invoke(task.data)
                _uiState.update {
                    it.copy(
                        messageAdded = true
                    )
                }
            }
            Response.Undefined -> Unit
        }
    }


    private fun getResponseWithImageInput( userPrompt: String, imageUrl: String, context: Context ) = viewModelScope.launch{
        _uiState.update {
            it.copy(
                messageAdded = false
            )
        }
        val task = getResponseWithImageInputUseCase.invoke(userPrompt, imageUrl, context )
        when(task) {
            is Response.Failure -> {
                _uiState.update {
                    it.copy(
                        isGettingResponse = false,
                        responseError = task.exception
                    )
                }
            }
            is Response.Success -> {
                _uiState.update {
                    it.copy(
                        isGettingResponse = false
                    )
                }
                addAiChatInCacheUseCase.invoke(task.data)
                _uiState.update {
                    it.copy(
                        messageAdded = true
                    )
                }
            }
            Response.Undefined -> Unit
        }
    }
}






















/*
when {
                        _uiState.value.imageUrl == null -> {
                            _uiState.update {
                                it.copy(
                                    chatList = it.chatList.toMutableList().apply {
                                        add(0, AiChat(prompt = userPrompt, imageUrl = null, isFromUser = true))
                                    },
                                    prompt = ""
                                )
                            }
                            getResponse(userPrompt)
                        }
                        else -> {
                            val imageUrl = _uiState.value.imageUrl!!
                            _uiState.update {
                                it.copy(
                                    chatList = it.chatList.toMutableList().apply {
                                        add(0, AiChat(prompt = userPrompt, imageUrl = imageUrl, isFromUser = true))
                                    },
                                    prompt = "",
                                    imageUrl = null
                                )
                            }
                            getResponseWithImageInput(userPrompt, imageUrl, event.context )
                        }
                    }
 */