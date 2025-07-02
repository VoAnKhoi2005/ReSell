package com.example.resell.ui.viewmodel.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerificationPopupViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var step by mutableStateOf(PhoneVerificationStep.EnterPhone)
        private set

    var phoneNumber by mutableStateOf("")
    var verificationId by mutableStateOf<String?>(null)
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown

    val error = mutableStateOf<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun onPhoneEntered(value: String) {
        phoneNumber = value
    }

    fun onNextStep() {
        step = PhoneVerificationStep.EnterOtp
        error.value =""
    }

    fun onBack() {
        step = PhoneVerificationStep.EnterPhone
    }

    fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        this.verificationId = verificationId
        this.resendToken = token
        onNextStep()
    }

    fun onVerificationFailed(message: String) {
        error.value = message
    }

    fun startCountdown(seconds: Int = 60) {
        viewModelScope.launch {
            for (i in seconds downTo 0) {
                _countdown.emit(i)
                delay(1000)
            }
        }
    }

    fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("0")) "+84" + phone.drop(1) else phone
    }

    fun verifyOtp(otp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val id = verificationId
        if (id != null) {
            val credential = PhoneAuthProvider.getCredential(id, otp)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = it.result?.user
                        user?.getIdToken(true)?.addOnSuccessListener { result ->
                            val token = result.token
                            if (token != null) {
                                viewModelScope.launch {
                                    val response = userRepository.firebaseAuth(token)
                                    response.fold(
                                        ifLeft = { error ->
                                            onError("Xác minh thất bại: ${error.message}")
                                        },
                                        ifRight = { fbAuthResponse ->
                                            if (fbAuthResponse.firstTimeLogin) {
                                               onSuccess()
                                            } else {
                                               onError("Số điện thoại đã được sử dụng!")
                                                onBack()
                                                phoneNumber =""
                                            }
                                        }
                                    )
                                }
                            } else {
                                error.value = "Token is null"
                            }
                            _isLoading.value = false
                        }?.addOnFailureListener {
                            error.value = "Failed to get token: ${it.localizedMessage}"
                            _isLoading.value = false
                        }
                    }
                    else onError(it.exception?.localizedMessage ?: "Lỗi xác minh")
                }
        } else {
            onError("Chưa có mã xác minh")
        }
    }
}

enum class PhoneVerificationStep {
    EnterPhone, EnterOtp
}


