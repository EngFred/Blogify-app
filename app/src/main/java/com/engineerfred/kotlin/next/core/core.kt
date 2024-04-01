package com.engineerfred.kotlin.next.core

import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat

fun hideSystemUI(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

//fun setFullScreen(window: Window, view: View) {
//    WindowCompat.setDecorFitsSystemWindows(window , false)
//    WindowInsetsControllerCompat(window, view).let {
//        it.hide(WindowInsetsCompat.Type.systemBars())
//        it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    }
//}

fun showSystemUI(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}