package com.example.resell

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReSell : Application() {
    override fun onCreate() {
        super.onCreate()
       /* Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("APP_CRASH", "Uncaught crash: ${throwable.message}", throwable)
            // Cho crash tiếp để logcat/Crashlytics bắt được lỗi
            thread.uncaughtExceptionHandler?.uncaughtException(thread, throwable)
        }*/

    }
}