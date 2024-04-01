package com.engineerfred.kotlin.next.domain.usecases.preferences

import com.engineerfred.kotlin.next.data.local.Preferences
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val preferences: Preferences
) {
    suspend operator fun invoke( isDarkTheme: Boolean ) = preferences.saveTheme(isDarkTheme)
}