package com.engineerfred.kotlin.next.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.engineerfred.kotlin.next.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Preferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        const val TAG = "Preferences"
    }

    suspend fun saveLoginInfo(saved: Boolean) {
        dataStore.edit{
            it[Constants.USER_LOGIN_INFO_KEY] = saved
        }
    }

    fun getLoginInfo(): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.USER_LOGIN_INFO_KEY] ?: true
        }.distinctUntilChanged()
            .flowOn( Dispatchers.IO )
            .catch {
                Log.v(TAG, "$it")
            }
    }

    suspend fun saveTheme(isDarkTheme: Boolean) {
        dataStore.edit{
            it[Constants.THEME_KEY] = isDarkTheme
        }
    }

    fun getSavedTheme(): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.THEME_KEY] ?: false
        }.distinctUntilChanged()
            .flowOn( Dispatchers.IO )
            .catch {
                Log.v(TAG, "$it")
            }
    }
}