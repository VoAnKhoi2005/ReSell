package com.example.resell.ui.screen.auth.phoneAuth

// AndroidX Composables & UI
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button // <-- Button đang không được dùng trong mã bạn cung cấp, nhưng tôi giữ lại nếu bạn có kế hoạch dùng.
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
// Android OS specific (cho KeyEvent.KEYCODE_DEL)
import android.view.KeyEvent // Cần thiết cho KeyEvent.KEYCODE_DEL
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

// Firebase (Nếu bạn vẫn đang dùng PhoneAuthScreen với Firebase, giữ lại)
// Các thư viện Firebase sau không được dùng trực tiếp trong mã `OtpCodeInput` hoặc `PhoneAuthScreen`
// của bạn. Nếu bạn dùng chúng ở phần khác của dự án hoặc có ý định mở rộng, hãy giữ lại.
// Hiện tại tôi sẽ bỏ comment những cái không được dùng trực tiếp trong snippet bạn gửi.
/*
import android.app.Activity
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import androidx.compose.ui.platform.LocalContext // Thường đi kèm với Toast/Activity
*/


// Internal project imports
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.R // Resource ID (cho R.drawable.image_received)
import com.example.resell.ui.theme.GreenButton
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.viewmodel.auth.phoneAuth.PhoneAuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


@Composable
fun PhoneAuthScreen(viewModel: PhoneAuthViewModel = hiltViewModel()) {
    val timeLeft by viewModel.countdown.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()

    val error by viewModel.error.collectAsState()
    val isCountingDown = timeLeft > 0

    fun startPhoneAuth() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                viewModel.signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                viewModel.onVerificationFailed("Verification failed: ${e.localizedMessage}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                viewModel.onCodeSent(verificationId, token)
            }
        }

        var formattedPhone = viewModel.formatPhoneNumber(viewModel.phoneNumber)
        formattedPhone = "+84111111111"
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        viewModel.startCountdown()
    }

    LaunchedEffect(Unit) {
        startPhoneAuth()
    }

    var otpResult by remember { mutableStateOf("") }
    Scaffold(topBar = {TopBar(
        showBackButton = true,
        onBackClick = {
            NavigationController.navController.popBackStack()
        }
    )}) { innerPadding ->
        Column( modifier = Modifier.fillMaxWidth()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                modifier = Modifier.width(250.dp)
                    .height(250.dp)
                    .padding(50.dp),
                painter = painterResource(id = R.drawable.image_received),
                contentDescription = "Mô tả ảnh"
            )
            Text(text = "Xác thực OTP")
            Text(text = "Nhập mã OTP chúng tôi đã gửi đến ${viewModel.phoneNumber}")
            OtpCodeInput { otp ->
                otpResult = otp
            }
            Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách

            // Dòng chữ "Không nhận được mã? Gửi lại mã (số giây đếm ngược)"
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Không nhận được mã? ")
                Text(
                    text = if (isCountingDown) "Gửi lại mã (${timeLeft}s)" else "Gửi lại mã",
                    color = if (isCountingDown) LightGray else GreenButton, // Màu sắc thay đổi
                    modifier = Modifier.clickable(enabled = !isCountingDown) {
                        // TODO: Thực hiện hành động gửi lại mã OTP ở đây
                      startPhoneAuth()
                    }
                )
            }


            Spacer(modifier = Modifier.height(32.dp)) // Khoảng cách cho nút

            // Nút Xác nhận
            Button(
                onClick = {
                    viewModel.verifyOtp(otpResult)
                },
                // Nút chỉ được kích hoạt khi OTP đã đủ 6 chữ số
                enabled = otpResult.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(50.dp)
            ) {
                Text("Xác nhận")
            }
        }
    }
}

@Composable
fun OtpCodeInput(
    otpLength: Int = 6,
    onOtpComplete: (String) -> Unit
) {
    val focusRequesters = List(otpLength) { FocusRequester() }
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .wrapContentWidth()
    ) {
        for (i in 0 until otpLength) {
            OutlinedTextField(
                value = otpValues[i],
                onValueChange = { newValue ->
                    // Kiểm tra xem newValue có phải là số và có độ dài <= 1
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        val oldValue = otpValues[i] // Lấy giá trị cũ của ô hiện tại

                        // Cập nhật giá trị của ô OTP
                        otpValues[i] = newValue

                        // Logic chuyển focus sau khi nhập số
                        // CHỈ CHUYỂN FOCUS KHI Ô HIỆN TẠI VỪA ĐƯỢC ĐIỀN (trước đó rỗng)
                        // VÀ KHÔNG PHẢI LÀ Ô CUỐI CÙNG.
                        if (newValue.isNotEmpty() && oldValue.isEmpty() && i < otpLength - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    }

                    // Logic xóa đã được xử lý bởi onPreviewKeyEvent, không cần ở đây

                    // Gọi callback khi OTP hoàn thành
                    if (otpValues.all { it.length == 1 }) {
                        onOtpComplete(otpValues.joinToString(""))
                    } else {
                        // Tùy chọn: Xóa OTP khi không đầy đủ nếu bạn muốn re-trigger sự kiện
                         onOtpComplete("")
                    }
                },
                modifier = Modifier
                    .width(48.dp)
                    .height(56.dp)
                    .focusRequester(focusRequesters[i])
                    .onPreviewKeyEvent { event ->
                        // Xử lý phím Backspace/Delete
                        if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL && event.type == KeyEventType.KeyDown) {
                            onOtpComplete("")
                            // Nếu ô hiện tại có giá trị, chỉ xóa giá trị và giữ focus
                            if (otpValues[i].isNotEmpty()) {
                                otpValues[i] = ""
                                return@onPreviewKeyEvent true // Tiêu thụ sự kiện
                            } else if (i > 0) {
                                // Nếu ô hiện tại rỗng, và không phải ô đầu tiên, di chuyển focus về ô trước đó
                                focusRequesters[i - 1].requestFocus()
                                otpValues[i-1]=""
                                return@onPreviewKeyEvent true // Tiêu thụ sự kiện
                            }

                        }
                        false // Để các phím khác (như số) được xử lý bởi onValueChange
                    }
                    .focusProperties {
                        // Hỗ trợ điều hướng bằng Tab/Shift+Tab nếu cần
                        if (i > 0) previous = focusRequesters[i - 1]
                        if (i < otpLength - 1) next = focusRequesters[i + 1]
                    },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}