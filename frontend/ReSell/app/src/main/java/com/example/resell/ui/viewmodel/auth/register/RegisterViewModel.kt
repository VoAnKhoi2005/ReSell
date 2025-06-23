package com.example.resell.ui.viewmodel.auth.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.resell.model.User
import com.example.resell.repository.UserRepository
import com.example.resell.store.FCMTokenManager
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.store.WebSocketManager
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository,
    private val webSocketManager: WebSocketManager,
    private val fcmTokenManager: FCMTokenManager
) : ViewModel() {

    // Đọc arguments từ navigation
    val type: String = savedStateHandle["type"] ?: "unknown"
    val id: String = savedStateHandle["id"] ?: "unknown"

    var userName by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var userNameError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    suspend fun onRegisterClick() {
        if (!validateForm()) return

        Log.d("RegisterViewModel", "type=$type id=$id user=$userName")
        if(type=="email"){
            //TODO: Xử lí đăng ký bằng email ở đây
            val response = userRepository.firebaseAuth(id, userName, password)
            response.fold(
                ifLeft = { networkError ->
                    val errors = networkError.errors
                    when {
                        errors?.containsKey("username") == true -> {
                            userNameError = "Tên người dùng đã được sử dụng"
                            onError("Đăng ký thất bại")
                        }

                        !errors.isNullOrEmpty() -> {
                            Log.e("Login", "Login failed: $errors")
                            onError("Đăng nhập thất bại: ${networkError.message}")
                        }

                        else -> {
                            Log.e("Login", "Login failed: ${networkError.message}")
                            onError("Đăng nhập thất bại: ${networkError.message}")
                        }
                    }
                },
                ifRight = { fbAuthResponse ->
                    onSuccess(fbAuthResponse.user)
                }
            )
        }

    }
    //TODO: Xử lý đăng nhập thành công
    private suspend fun onSuccess(user : User?){
        webSocketManager.connect()
        fcmTokenManager.fetchAndSendToken()
        ReactiveStore<User>().set(user);
        NavigationController.navController.navigate(Screen.Main.route)
    }
    //TODO: Xử lý đăng nhập với firebase thất bại
    private fun onError(message : String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    private fun validateForm(): Boolean {
        var isValid = true

        // Kiểm tra tên người dùng
        if (userName.length !in 8..12) {
            userNameError = "Tên người dùng phải từ 8 đến 12 ký tự"
            isValid = false
        } else if (!userName.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            userNameError = "Tên người dùng không được chứa ký tự đặc biệt"
            isValid = false
        } else {
            userNameError = null
        }

        // Kiểm tra mật khẩu
        if (password.length !in 8..16) {
            passwordError = "Mật khẩu phải từ 8 đến 16 ký tự"
            isValid = false
        } else {
            passwordError = null
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPassword != password) {
            confirmPasswordError = "Mật khẩu không khớp"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        return isValid
    }

}