package com.engineerfred.kotlin.next.presentation.navigation

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val destinationScreen: String
)
