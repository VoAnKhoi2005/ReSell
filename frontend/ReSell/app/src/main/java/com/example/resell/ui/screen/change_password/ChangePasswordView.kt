package com.example.resell.ui.screen.change_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.resell.R
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen() {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Đổi mật khẩu",
                showBackButton = true,
                onBackClick = { NavigationController.navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White2)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordField(
                label = "Mật khẩu hiện tại",
                password = currentPassword,
                onPasswordChange = {
                    currentPassword = it
                    currentPasswordError = null
                },
                isVisible = currentVisible,
                onToggleVisibility = { currentVisible = !currentVisible },
                error = currentPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Mật khẩu mới",
                password = newPassword,
                onPasswordChange = {
                    newPassword = it
                    newPasswordError = null
                },
                isVisible = newVisible,
                onToggleVisibility = { newVisible = !newVisible },
                error = newPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Nhập lại mật khẩu mới",
                password = confirmPassword,
                onPasswordChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                isVisible = confirmVisible,
                onToggleVisibility = { confirmVisible = !confirmVisible },
                error = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (successMessage != null) {
                Text(text = successMessage!!, color = Color(0xFF00C853))
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    val isValid = validatePasswordFields(
                        currentPassword,
                        newPassword,
                        confirmPassword
                    ) { field, message ->
                        when (field) {
                            "current" -> currentPasswordError = message
                            "new" -> newPasswordError = message
                            "confirm" -> confirmPasswordError = message
                        }
                    }

                    if (isValid) {
                        scope.launch {
                            isLoading = true
                            delay(1000) // giả lập gọi API
                            isLoading = false
                            successMessage = "Đổi mật khẩu thành công"
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Cập nhật", color = White)
            }
        }
    }
}

@Composable
fun PasswordField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        painter = painterResource(
                            id = if (isVisible) R.drawable.eye_off_1 else R.drawable.eye_1
                        ),
                        contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = DarkBlue
                    )
                }
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
        }
    }
}

private fun validatePasswordFields(
    current: String,
    new: String,
    confirm: String,
    onError: (field: String, message: String) -> Unit
): Boolean {
    var isValid = true

    if (current.isBlank()) {
        onError("current", "Vui lòng nhập mật khẩu hiện tại")
        isValid = false
    }

    if (new.isBlank()) {
        onError("new", "Vui lòng nhập mật khẩu mới")
        isValid = false
    }

    if (confirm.isBlank()) {
        onError("confirm", "Vui lòng nhập lại mật khẩu mới")
        isValid = false
    } else if (new != confirm) {
        onError("confirm", "Mật khẩu không khớp")
        isValid = false
    }

    return isValid
}
