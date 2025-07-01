package com.example.resell.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.ui.screen.auth.phoneAuth.OtpCodeInput
import com.example.resell.ui.theme.GreenButton
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.viewmodel.components.PhoneVerificationPopupViewModel
import com.example.resell.ui.viewmodel.components.PhoneVerificationStep
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun PhoneVerificationPopup(
    viewModel: PhoneVerificationPopupViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onVerified: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val step = viewModel.step
    val countdown by viewModel.countdown.collectAsState()
    val isCountingDown = countdown > 0
    val error = viewModel.error.value
    var otpResult by remember { mutableStateOf("") }

    fun startPhoneAuth() {
        val formattedPhone = viewModel.formatPhoneNumber(viewModel.phoneNumber)
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        onVerified(viewModel.phoneNumber)
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                viewModel.onVerificationFailed("Gửi mã thất bại: ${e.localizedMessage}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                viewModel.onCodeSent(verificationId, token)
            }
        }

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        viewModel.startCountdown()
    }

    LaunchedEffect(step) {
        if (step == PhoneVerificationStep.EnterOtp) {
            startPhoneAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomCenter)
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Top bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                        Text(
                            text = if (step == PhoneVerificationStep.EnterPhone) "Nhập số điện thoại" else "Xác minh OTP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(48.dp)) // để căn giữa tiêu đề
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (error != null) {
                        Text(text = error, color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                    }

                    when (step) {
                        PhoneVerificationStep.EnterPhone -> {
                            OutlinedTextField(
                                value = viewModel.phoneNumber,
                                onValueChange = viewModel::onPhoneEntered,
                                label = { Text("Số điện thoại") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { startPhoneAuth() },
                                enabled = viewModel.phoneNumber.length == 10,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tiếp tục")
                            }
                        }

                        PhoneVerificationStep.EnterOtp -> {
                            Text("Nhập mã OTP đã gửi đến ${viewModel.phoneNumber}")
                            Spacer(Modifier.height(8.dp))
                            OtpCodeInput { otp -> otpResult = otp }
                            Spacer(Modifier.height(16.dp))

                            Row {
                                Text(text = "Không nhận được mã? ")
                                Text(
                                    text = if (isCountingDown) "Gửi lại mã (${countdown}s)" else "Gửi lại mã",
                                    color = if (isCountingDown) LightGray else GreenButton,
                                    modifier = Modifier.clickable(enabled = !isCountingDown) {
                                        startPhoneAuth()
                                    }
                                )
                            }

                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    viewModel.verifyOtp(
                                        otp = otpResult,
                                        onSuccess = { onVerified(viewModel.phoneNumber) },
                                        onError = { viewModel.onVerificationFailed(it) }
                                    )
                                },
                                enabled = otpResult.length == 6,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Xác nhận")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "← Quay lại",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { viewModel.onBack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
