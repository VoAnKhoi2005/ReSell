package com.example.resell.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun HideNavigationBar() {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    LaunchedEffect(Unit) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE.also { controller.systemBarsBehavior = it }

        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }
}