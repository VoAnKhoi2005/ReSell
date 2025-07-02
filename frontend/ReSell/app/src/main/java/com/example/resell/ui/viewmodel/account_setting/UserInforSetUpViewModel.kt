package com.example.resell.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.UpdateProfileRequest
import com.example.resell.model.User
import com.example.resell.repository.UserRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
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

    fun saveChanges(
        name: String,
        phone: String,
        email: String,
    ) {
        viewModelScope.launch {
            val request = UpdateProfileRequest(
                fullName = name,
                phone = if (_currentUser.value?.phone.isNullOrBlank()) phone else null,
                email = if (_currentUser.value?.email.isNullOrBlank()) email else null
            )

            val result = userRepository.updateInfo(request)
            result.fold(
                {
                    EventBus.sendEvent(Event.Toast("Cập nhật thất bại: ${it.message}"))
                },
                {
                    val updatedUser = _currentUser.value?.copy(
                        fullName = name,
                        phone = null,
                        email = email
                    )
                    updatedUser?.let { user -> userStore.set(user) }
                    EventBus.sendEvent(Event.Toast("Cập nhật thành công"))
                }
            )
        }
    }

}
