package com.example.resell.ui.screen.auth.phoneAuth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun PhoneAuthScreen() {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
   // val auth = FirebaseAuth.getInstance()
    var verificationId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Số điện thoại (+84...)") }
        )

        Button(onClick = {
//            val options = PhoneAuthOptions.newBuilder(auth)
//                .setPhoneNumber(phoneNumber)
//                .setTimeout(60L, TimeUnit.SECONDS)
//                .setActivity(context as Activity)
//                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                        // Tự động xác minh (nếu có)
//                        auth.signInWithCredential(credential)
//                    }
//
//                    override fun onVerificationFailed(e: FirebaseException) {
//                        Toast.makeText(context, "Gửi OTP thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onCodeSent(verificationId_: String, token: PhoneAuthProvider.ForceResendingToken) {
//                        verificationId = verificationId_
//                        Toast.makeText(context, "Đã gửi OTP", Toast.LENGTH_SHORT).show()
//                    }
//                })
//                .build()
//            PhoneAuthProvider.verifyPhoneNumber(options)
        }) {
            Text("Gửi OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otpCode,
            onValueChange = { otpCode = it },
            label = { Text("Mã OTP") }
        )

        Button(onClick = {
//            val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
//            auth.signInWithCredential(credential)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Sai mã OTP", Toast.LENGTH_SHORT).show()
//                    }
//                }
        }) {
            Text("Xác minh")
        }
    }
}
