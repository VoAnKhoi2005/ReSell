package com.example.resell.ui.viewmodel.auth.phoneAuth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.UserRepository
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var _verificationId: String? = null
    private var _resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        _verificationId = verificationId
        _resendToken = token
        _isLoading.value = false
        startCountdown()
    }

    fun onVerificationFailed(errorMessage: String) {
        _error.value = errorMessage
        _isLoading.value = false
    }

    fun verifyOtp(code: String) {
        val credential = _verificationId?.let {
            PhoneAuthProvider.getCredential(it, code)
        } ?: return

        signInWithCredential(credential)
    }

    fun signInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        user?.getIdToken(true)?.addOnSuccessListener { result ->
                                val token = result.token
                                if (token != null) {
                                    viewModelScope.launch {
                                        saveToDB(token)
                                    }
                                } else {
                                    _error.value = "Token is null"
                                }
                                _isLoading.value = false
                            }?.addOnFailureListener {
                                _error.value = "Failed to get token: ${it.localizedMessage}"
                                _isLoading.value = false
                            }
                    } else {
                        _isLoading.value = false
                        _error.value = task.exception?.localizedMessage
                    }
                }
        }
    }

    private suspend fun saveToDB(firebaseIdToken: String) {
        val response = userRepository.firebaseAuth(firebaseIdToken)
        response.fold(
            ifLeft = { error ->
                Log.e("Login", "Login failed: ${error.message}")
                //onError("Đăng nhập thất bại: ${error.message}")
            },
            ifRight = { fbAuthResponse ->
                if (fbAuthResponse.firstTimeLogin) {
                    //TODO Di chuyển qua trang register để lấy username
                } else {
                    //onSuccess(fbAuthResponse.user)
                }
            }
        )
    }

    fun startCountdown() {
        viewModelScope.launch {
            _countdown.value = 30
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value -= 1
            }
        }
    }
}
