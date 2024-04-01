package com.engineerfred.kotlin.next.presentation.screens.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.usecases.users.UpdateUserInfoUseCase
import com.engineerfred.kotlin.next.utils.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid

    fun onEvent( event: EditProfileUiEvents ) {
        when(event){
            is EditProfileUiEvents.AboutChanged -> {
                _uiState.update {
                    it.copy(
                        about = event.about.trim(),
                        updateError = null
                    )
                }
            }
            is EditProfileUiEvents.FirstNameChanged -> {
                _uiState.update {
                    it.copy(
                        firstName = event.firstName.trim(),
                        updateError = null
                    )
                }
            }
            is EditProfileUiEvents.LastNameChanged -> {
                _uiState.update {
                    it.copy(
                        lastName = event.lastName.trim(),
                        updateError = null
                    )
                }
            }

            EditProfileUiEvents.UpdateButtonClicked -> {
                _uiState.update {
                    it.copy(
                        isUpdating = true,
                        updateError = null
                    )
                }
                updateUserProfile()
            }
        }
    }

    private fun updateUserProfile() = viewModelScope.launch( Dispatchers.IO ) {
        loggedInUserId?.let {
            val task = updateUserInfoUseCase.invoke(it, _uiState.value.firstName, _uiState.value.lastName, _uiState.value.about)
            when(task) {
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateError = task.exception
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccessful = true
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    fun initializeUiState( firstName: String, lastName: String, about: String? ) {
        _uiState.update {
            it.copy(
                firstName = firstName,
                lastName = lastName,
                about = about
            )
        }
    }

}