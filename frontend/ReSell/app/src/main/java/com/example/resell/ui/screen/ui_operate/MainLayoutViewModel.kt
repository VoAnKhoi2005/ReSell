package com.example.resell.ui.screen.ui_operate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.UpdateProfileRequest
import com.example.resell.model.User
import com.example.resell.repository.CategoryRepository
import com.example.resell.repository.PostRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainLayoutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val userStore = ReactiveStore<User>()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            userStore.item.collect { user ->
                _currentUser.value = user
            }
        }
    }
    private val _isOpenPopup = MutableStateFlow(false)
    val isOpenPopup: StateFlow<Boolean> = _isOpenPopup
    fun onAddClicked(){
        if (!currentUser.value!!.phone.isNullOrBlank()){
            NavigationController.navController.navigate(Screen.Add.route)
        }
        else{
            _isOpenPopup.value = true
        }
    }
    fun closePopUp(){
        _isOpenPopup.value = false
    }
    fun onAddPhone(phone: String){
        viewModelScope.launch {
            val request = UpdateProfileRequest(phone = phone)
            val test = userRepository.updateInfo(request)
            test.fold(
                {
                    EventBus.sendEvent(Event.Toast(it.message?:"Lỗi update info"))
                },
                {
                    _isOpenPopup.value = false
                    _currentUser.value?.let { user ->
                        val updatedUser = user.copy(phone = phone)
                        ReactiveStore<User>().set(updatedUser)
                    }

                }
            )
        }

    }
}