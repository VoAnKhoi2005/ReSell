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

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    fun changePassword() {
        // Kiểm tra mật khẩu mới nhập lại có trùng không
        if (newPassword != confirmPassword) {
            errorMessage = "Mật khẩu mới không khớp"
            return
        }

        // Gọi API
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
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                    } else {
                        errorMessage = "Mật khẩu cũ không chính xác"
                    }
                }
            )
        }
    }
}
