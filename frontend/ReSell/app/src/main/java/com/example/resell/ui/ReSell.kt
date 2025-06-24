package com.example.resell.ui

import android.app.Application
import com.example.resell.repository.AddressRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class ReSell : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}