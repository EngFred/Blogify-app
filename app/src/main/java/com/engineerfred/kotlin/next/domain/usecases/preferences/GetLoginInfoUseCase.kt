package com.engineerfred.kotlin.next.domain.usecases.preferences

import com.engineerfred.kotlin.next.data.local.Preferences
import javax.inject.Inject

class GetLoginInfoUseCase @Inject constructor(
    private val preferences: Preferences
) {
    operator fun invoke() = preferences.getLoginInfo()
}