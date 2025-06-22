package com.example.resell.ui.viewmodel.auth.phoneAuth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.ui.navigation.NavigationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    var phoneNumber = ""




    init {
        if (phoneNumber.isNotBlank()) {
            sendOtp(false)
        }
    }

    fun sendOtp(resend: Boolean = false) {
        //TODO gửi mã OTP

        //Nếu gửi thành công tiến hành đếm ngược
        startCountdown()
    }
    fun verifyOtp(code: String) {

    }

    private fun startCountdown() {
        viewModelScope.launch {
            _countdown.value = 30
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value -= 1
            }
        }
    }
}
