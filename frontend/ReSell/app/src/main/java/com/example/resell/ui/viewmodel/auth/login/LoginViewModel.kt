package com.example.resell.ui.viewmodel.auth.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope // Sử dụng viewModelScope thay vì lifecycleScope của Activity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.resell.R
import com.example.resell.model.LoginType
import com.example.resell.model.User
import com.example.resell.repository.AddressRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.FCMTokenManager
import com.example.resell.store.ReactiveStore
import com.example.resell.store.WebSocketManager
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.tasks.await

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val fcmTokenManager: FCMTokenManager,
    private val webSocketManager: WebSocketManager,
    private val repo: AddressRepository
) : AndroidViewModel(application) {

    private val context by lazy { application.applicationContext }

    private val auth = Firebase.auth
    private val _isLoginLoading = MutableStateFlow(false)
    val isLoginLoading: StateFlow<Boolean> = _isLoginLoading
    private val _state = MutableStateFlow(LoginViewState())
    val state = _state.asStateFlow()

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    /**
     * Khởi chạy quá trình đăng nhập Google thông qua Credential Manager.
     * @param onSuccess Callback khi đăng nhập Firebase thành công, trả về FirebaseUser.
     * @param onError Callback khi có lỗi, trả về thông báo lỗi.
     */
    fun launchUsernameSignIn(identifier: String, password: String){
        viewModelScope.launch {
            _isLoginLoading.value = true
            val result = userRepository.loginUser(identifier, password,LoginType.USERNAME)

            result.fold(
                { error -> // Left - thất bại
                   _loginError.value = when(error.code){
                       400 -> "Sai thông tin đăng nhập"
                       401 -> "Không có quyền truy cập"
                       500 -> "Lỗi hệ thống server"
                       else -> "Đã xảy ra lỗi không xác định (mã ${error.code})"
                   }
                    error.errors?.forEach { (field, msg) ->
                        Log.e("Login", "$field: $msg")
                    }
                    _isLoginLoading.value = false
                },
                { response -> // Right - thành công
                   onSuccess(response.user)
                    _isLoginLoading.value = false
                }
            )
        }
    }

    fun launchGoogleSignIn(result: GetCredentialResponse) {
        viewModelScope.launch {
            try {
//                val response = repo.getAllProvinces()
//                response.fold(
//                    ifLeft = {provinces ->
//                        val pro = provinces
//                    },
//                    ifRight = {}
//                )

                val googleIdToken = handleSignIn(result)
                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(credential).await()

                val firebaseIdToken = authResult.user?.getIdToken(true)?.await()?.token
                if (firebaseIdToken == null) {
                    onError("Không thể lấy Firebase ID token")
                    return@launch
                }

                saveToDB(firebaseIdToken)
            } catch (e: GetCredentialException){
                if (e.type == "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL") {
                    onError("Đăng nhập thất bại: Không tìm thấy tài khoản Google nào trên thiết bị")
                } else {
                    onError("Đăng nhập thất bại: ${e.type}")
                    Log.e(TAG,"${e.type}")
                }
            } catch (e: Exception) {
                onError("Đăng nhập thất bại: ${e.message}")
                Log.e(TAG,"${e.message}")
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse): String {
        var fbIDToken = ""

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        fbIDToken = googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("Firebase:", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("Firebase:", "Unexpected type of credential")
                }
            }

            else -> {
                Log.e("Firebase:", "Unexpected type of credential")
            }
        }

        return fbIDToken
    }

    private suspend fun saveToDB(firebaseIdToken: String) {
        val response = userRepository.firebaseAuth(firebaseIdToken)
        response.fold(
            ifLeft = { error ->
                Log.e("Login", "Login failed: ${error.message}")
                onError("Đăng nhập thất bại: ${error.message}")
            },
            ifRight = { fbAuthResponse ->
                if (fbAuthResponse.firstTimeLogin) {
                    //TODO Di chuyển qua trang register để lấy username
                    NavigationController.navController.navigate(Screen.Register.route + "/email/${firebaseIdToken}")
                } else {
                    onSuccess(fbAuthResponse.user)
                }
            }
        )
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

    companion object {
        private const val TAG = "LoginViewModel"
    }
}