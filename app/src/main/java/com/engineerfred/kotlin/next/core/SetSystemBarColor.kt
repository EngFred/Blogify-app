package com.engineerfred.kotlin.next.core

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SetSystemBarColor(
    color: androidx.compose.ui.graphics.Color,
    isDarkTheme: Boolean = true
) {
    val view = LocalView.current

    LaunchedEffect(key1 = true) {
        val window = (view.context as Activity).window
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
    }

}