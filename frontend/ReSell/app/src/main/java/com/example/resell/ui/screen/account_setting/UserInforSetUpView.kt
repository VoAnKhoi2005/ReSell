package com.example.resell.ui.screen.account_setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.profile.AccountSettingViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.resell.ui.components.PhoneVerificationPopup
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.address.InfoBoxField
import com.example.resell.ui.theme.Red

@Composable
fun AccountSettingScreen(
    viewModel: AccountSettingViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val user by viewModel.currentUser.collectAsState()

    var showPhoneVerification by remember { mutableStateOf(false) }

    val phone by viewModel.phone.collectAsState()
    val email by viewModel.email.collectAsState()
    val fullName by viewModel.name.collectAsState()
    val defaultAddressText by viewModel.defaultAddressText.collectAsState()


    // Lỗi
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val isPhoneEditable = user?.phone.isNullOrBlank()
    val isEmailEditable = user?.email.isNullOrBlank()

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Cài đặt tài khoản",
                showBackButton = true,
                onBackClick = {
                    NavigationController.navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White2)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text("Thông tin cá nhân", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            InputWithLabel(
                label = "Họ và tên",
                value = fullName,
                onValueChange = {
                    viewModel.setName(it)
                    nameError = null
                },
                errorText = nameError
            )

            InputWithLabel(
                label = "Số điện thoại",
                value = if (phone.isNullOrBlank()) "Nhập số điện thoại" else phone,
                onValueChange = {},
                enabled = false,
                onClick = {
                    if (isPhoneEditable) {
                        showPhoneVerification = true
                    }
                },
                errorText = phoneError
            )

                Spacer(Modifier.height(12.dp))

            InputWithLabel(
                label = "Email",
                value = email,
                onValueChange = {
                    viewModel.setEmail(it)
                    emailError = null
                },
                keyboardType = KeyboardType.Email,
                enabled = isEmailEditable,
                errorText = emailError
            )

                Spacer(Modifier.height(12.dp))
            InputWithLabel(
                label = "Địa chỉ mặc định",
                value = defaultAddressText,
                onValueChange = {},
                enabled = false
            )
            Spacer(Modifier.height(12.dp))

            InfoBoxField(
                label = "Mật khẩu",
                value = "********",
                onClick = {
                    NavigationController.navController.navigate(Screen.ChangePassWordScreen.route)
                }
            )


            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val isValid = validateProfileInput(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        onError = { field, msg ->
                            when (field) {
                                "name" -> nameError = msg
                                "email" -> emailError = msg
                                "phone" -> phoneError = msg
                            }
                        }
                    )

                    if (isValid) {
                        viewModel.saveChanges()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Lưu thay đổi", color = White)
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    if (showPhoneVerification) {
        PhoneVerificationPopup(
            onDismiss = { showPhoneVerification = false },
            onVerified = { verifiedPhone ->
                showPhoneVerification = false
                viewModel.setPhone(verifiedPhone)
            }
        )
    }
}

@Composable
fun InputWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    errorText: String? = null
) {
    Text(text = label, style = MaterialTheme.typography.labelMedium)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        enabled = enabled,
        readOnly = !enabled,
        isError = errorText != null,
    )

    if (!errorText.isNullOrEmpty()) {
        Text(
            text = errorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
    }
    else {
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun validateProfileInput(
    fullName: String,
    email: String,
    phone: String,
    onError: (field: String, message: String) -> Unit
): Boolean {
    var isValid = true

    if (fullName.isBlank()) {
        onError("name", "Họ và tên không được để trống")
        isValid = false
    }

    if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onError("email", "Email không hợp lệ")
        isValid = false
    }

     if (!phone.matches(Regex("^0\\d{9}$"))) {
        onError("phone", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
        isValid = false
    }

    return isValid
}
