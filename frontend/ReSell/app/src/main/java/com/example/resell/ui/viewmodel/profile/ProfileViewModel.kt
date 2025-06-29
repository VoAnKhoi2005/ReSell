package com.example.resell.ui.viewmodel.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.User
import com.example.resell.model.UserStatResponse
import com.example.resell.repository.UserRepository
import com.example.resell.store.AuthTokenManager
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authTokenManager: AuthTokenManager,
    private val userRepository: UserRepository
) : ViewModel() {
    var statUser by mutableStateOf<UserStatResponse?>(null)
    init {
        viewModelScope.launch {
            val userId = ReactiveStore<User>().item.value?.id
            if (!userId.isNullOrEmpty()) {
                val stat = userRepository.getUserStat(userId)
               stat.fold(
                   {
                       error->
                       Log.e("Profile","${error.message}")
                   },
                   {
                       statUser = it
                   }
               )
            }
        }
    }

    fun logout(){
        ReactiveStore.clearAll()
        NavigationController.navController.navigate(Screen.Login.route)
        authTokenManager.clearToken()
        viewModelScope.launch {
            EventBus.sendEvent(Event.Toast("Đăng xuất thành công"))
        }
    }
}