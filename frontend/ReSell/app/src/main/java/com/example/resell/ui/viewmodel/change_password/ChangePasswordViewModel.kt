package com.example.resell.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var currentPasswordError by mutableStateOf<String?>(null)
    var newPasswordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    private fun validate(): Boolean {
        var valid = true

        if (currentPassword.isBlank()) {
            currentPasswordError = "Vui lòng nhập mật khẩu hiện tại"
            valid = false
        } else currentPasswordError = null

        if (newPassword.isBlank()) {
            newPasswordError = "Vui lòng nhập mật khẩu mới"
            valid = false
        } else newPasswordError = null

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Vui lòng nhập lại mật khẩu mới"
            valid = false
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "Mật khẩu không khớp"
            valid = false
        } else confirmPasswordError = null

        return valid
    }

    fun changePassword() {
        if (!validate()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            val result = userRepository.changePassword(currentPassword, newPassword)
            isLoading = false

            result.fold(
                { error ->
                    errorMessage = "Mật khẩu cũ không đúng hoặc có lỗi xảy ra"
                },
                { success ->
                    if (success) {
                        successMessage = "Đổi mật khẩu thành công"
                        // Reset fields và errors
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        currentPasswordError = null
                        newPasswordError = null
                        confirmPasswordError = null
                    } else {
                        errorMessage = "Mật khẩu cũ không chính xác"
                    }
                }
            )
        }
    }
}
