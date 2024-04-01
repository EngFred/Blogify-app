package com.engineerfred.kotlin.next.presentation.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.usecases.users.GetUsersUseCase
import com.engineerfred.kotlin.next.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState = _uiState.asStateFlow()


    init {
        getFriends()
    }

    private fun getFriends() = viewModelScope.launch {
        getUsersUseCase.invoke().collectLatest { apiResponse ->
            when(apiResponse){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            loadError = apiResponse.exception,
                            isLoading = false
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            friends = apiResponse.data
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    fun onEvent( event: MessagesUiEvents ) {
        when(event){
            MessagesUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        loadError = null
                    )
                }
                getFriends()
            }
        }
    }

}