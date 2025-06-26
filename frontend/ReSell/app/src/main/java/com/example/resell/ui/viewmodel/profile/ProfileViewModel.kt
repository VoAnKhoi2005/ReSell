package com.example.resell.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.store.AuthTokenManager
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authTokenManager: AuthTokenManager
) : ViewModel() {

    fun logout(){
        ReactiveStore.clearAll()
        NavigationController.navController.navigate(Screen.Login.route)
        authTokenManager.clearToken()
        viewModelScope.launch {
            EventBus.sendEvent(Event.Toast("Đăng xuất thành công"))
        }
    }
}