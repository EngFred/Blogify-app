package com.engineerfred.kotlin.next.presentation.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.kotlin.next.domain.usecases.preferences.GetLoginInfoUseCase
import com.engineerfred.kotlin.next.domain.usecases.preferences.SaveLoginInfoUseCase
import com.engineerfred.kotlin.next.domain.usecases.preferences.SaveThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveLoginInfoUseCase: SaveLoginInfoUseCase,
    private val getLoginInfoUseCase: GetLoginInfoUseCase,
    private val saveThemeUseCase: SaveThemeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()


    companion object {
        private val TAG = SettingsViewModel::class.java.simpleName
    }


    init {
        getLoginUserInfo()
    }

    fun onEvent( event: SettingsUiEvents ) {
        when(event){
            SettingsUiEvents.SaveUserLoginInfoStateChanged -> {
                _uiState.update {
                    it.copy(
                        isSavingUserInfo = true,
                        saveInfo = !it.saveInfo
                    )
                }
                saveLoginInfo()
            }
            SettingsUiEvents.ThemeChanged -> {

            }
        }
    }

    private fun saveLoginInfo() = viewModelScope.launch( Dispatchers.IO ) {
       saveLoginInfoUseCase.invoke(_uiState.value.saveInfo)
        _uiState.update {
            it.copy(
                isSavingUserInfo = false,
            )
        }
    }


    private fun getLoginUserInfo() = viewModelScope.launch {
        getLoginInfoUseCase.invoke().collectLatest { saveInfo ->
            _uiState.update {
                it.copy(
                    saveInfo = saveInfo,
                    isSavingUserInfo = false
                )
            }
            Log.i(TAG, "WasLoginInfoSave: $saveInfo")
        }
    }

    fun saveTheme(newTheme: Boolean) = viewModelScope.launch( Dispatchers.IO ) {
        saveThemeUseCase.invoke(newTheme)
    }

}