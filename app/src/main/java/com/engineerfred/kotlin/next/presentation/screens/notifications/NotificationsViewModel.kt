package com.engineerfred.kotlin.next.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.usecases.notifications.GetUserNotificationsUseCase
import com.engineerfred.kotlin.next.domain.usecases.notifications.UpdateNotificationUseCase
import com.engineerfred.kotlin.next.utils.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getUserNotificationsUseCase: GetUserNotificationsUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    private val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid


    init {
        getUserNotifications()
    }

    fun onEvent( event: NotificationsUiEvents ) {
        when(event){
            NotificationsUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        loadError = null,
                        notifications = emptyList()
                    )
                }
                getUserNotifications()
            }

            is NotificationsUiEvents.NotificationClicked -> {
                if( !event.isNotificationRead ) {
                    markNotificationAsRead(event.notificationId)
                }
            }
        }
    }

    private fun markNotificationAsRead(notificationId:String) = viewModelScope.launch( Dispatchers.IO ) {
        updateNotificationUseCase.invoke(notificationId)
    }

    private fun getUserNotifications() = viewModelScope.launch {
        if ( loggedInUserId != null ) {
            getUserNotificationsUseCase.invoke(loggedInUserId).collectLatest { apiResponse ->
                when(apiResponse){
                    is Response.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = apiResponse.exception,
                                notifications = emptyList()
                            )
                        }
                    }
                    is Response.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                notifications = apiResponse.data
                            )
                        }
                    }
                    Response.Undefined -> Unit
                }
            }
        }
    }
}