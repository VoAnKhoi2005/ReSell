package com.example.resell.ui.viewmodel.auth.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope // Sử dụng viewModelScope thay vì lifecycleScope của Activity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
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
import com.example.resell.R // Đảm bảo đúng package R của bạn
import com.example.resell.model.LoginType
import com.example.resell.model.User
import com.example.resell.repository.UserRepository // Giữ nguyên repository của bạn
import com.example.resell.store.DataStore
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val myRepository: UserRepository // Giữ nguyên repository của bạn
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _state = MutableStateFlow(LoginViewState())
    val state = _state.asStateFlow()
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow() // Đảm bảo sử dụng .asStateFlow()
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError
    // Khởi tạo Google Sign-In request một lần
    private val googleRequest: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                // Sử dụng R.string.web_client_id của bạn (Server Client ID từ Google Cloud Console)
                .setServerClientId(context.getString(R.string.web_client_id))
                // Chỉ hiển thị các tài khoản đã từng đăng nhập
                .setFilterByAuthorizedAccounts(true)
                .build()
        ).build()

    /**
     * Khởi chạy quá trình đăng nhập Google thông qua Credential Manager.
     * @param onSuccess Callback khi đăng nhập Firebase thành công, trả về FirebaseUser.
     * @param onError Callback khi có lỗi, trả về thông báo lỗi.
     */
    fun launchUsernameSignIn(identifier: String, password: String){
        viewModelScope.launch {

            val result = myRepository.loginUser(identifier, password,LoginType.USERNAME)

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
                },
                { response -> // Right - thành công
                   onSuccess(response.user)
                }
            )
        }
    }
    suspend fun launchGoogleSignIn() {
        viewModelScope.launch { // Sử dụng viewModelScope cho các coroutine trong ViewModel
            try {
                // Bước 1: Lấy thông tin đăng nhập từ Credential Manager
                val result = credentialManager.getCredential(context, googleRequest)
                val credential = result.credential

                // Bước 2: Xử lý thông tin đăng nhập nhận được
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    val idToken = googleIdTokenCredential.idToken // Lấy ID Token

                    // Debugging log: Kiểm tra xem ID Token có null không
                    Log.d(TAG, "ID Token retrieved from Google: $idToken")

                    if (idToken != null) {
                        // Bước 3: Xác thực ID Token với Firebase
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        // Trường hợp này xảy ra nếu Google Play Services không trả về ID Token
                        val errorMessage = "Failed to retrieve Google ID Token (ID Token is null)."
                        Log.e(TAG, errorMessage)
                        onError(errorMessage)
                    }
                } else {
                    val errorMessage = "Credential is not a Google ID Token credential."
                    Log.w(TAG, errorMessage)
                    onError(errorMessage)
                }
            } catch (e: GetCredentialException) {
                val errorMessage = "Error getting Google credential: ${e.localizedMessage}"
                Log.e(TAG, errorMessage, e)
                onError(errorMessage)
            } catch (e: Exception) {
                // Bắt các lỗi chung khác
                val errorMessage = "An unexpected error occurred during Google sign-in: ${e.localizedMessage}"
                Log.e(TAG, errorMessage, e)
                onError(errorMessage)
            }
        }
    }

    /**
     * Xác thực ID Token với Firebase Authentication.
     * @param idToken ID Token từ Google.
     * @param onError Callback khi có lỗi, trả về thông báo lỗi.
     */
    private fun firebaseAuthWithGoogle(
        idToken: String
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đăng nhập Firebase thành công
                    Log.d(TAG, "Firebase Auth with Google: SUCCESS")
                    val currentUser = auth.currentUser


                    _user.value = currentUser // Cập nhật StateFlow _user
                    //TODO: Lấy user
                  //  onSuccess(currentUser) // Gọi callback thành công
                } else {
                    // Đăng nhập Firebase thất bại
                    val errorMessage = "Firebase Authentication failed: ${task.exception?.localizedMessage}"
                    Log.e(TAG, errorMessage, task.exception)
                    onError(errorMessage) // Gọi callback lỗi
                }
            }
    }

    //TODO: Xử lý đăng nhập thành công
    private fun onSuccess(user : User?){

       DataStore.user = user
        NavigationController.navController.navigate(Screen.Main.route)
    }
    //TODO: Xử lý đăng nhập với firebase thất bại
    private fun onError(message : String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Đăng xuất người dùng khỏi Firebase và xóa trạng thái Credential Manager.
     */
    fun signOut() {
        auth.signOut()
        _user.value = null

        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                Log.d(TAG, "Credential Manager state cleared.")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing credential state: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}