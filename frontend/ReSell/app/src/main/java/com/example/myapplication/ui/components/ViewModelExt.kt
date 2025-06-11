package com.example.myapplication.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import util.EventBus

fun ViewModel.sendEvent(event: Any){
    viewModelScope.launch {
        EventBus.sendEvent(event)
    }
}