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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resell.R
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.MainButton
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.ChangePasswordViewModel
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
) {
    val viewModel: ChangePasswordViewModel = hiltViewModel()
    val currentPassword by remember { derivedStateOf { viewModel.currentPassword } }
    val newPassword by remember { derivedStateOf { viewModel.newPassword } }
    val confirmPassword by remember { derivedStateOf { viewModel.confirmPassword } }

    val currentPasswordError by remember { derivedStateOf { viewModel.currentPasswordError } }
    val newPasswordError by remember { derivedStateOf { viewModel.newPasswordError } }
    val confirmPasswordError by remember { derivedStateOf { viewModel.confirmPasswordError } }

    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }
    val successMessage by remember { derivedStateOf { viewModel.successMessage } }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

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
                onPasswordChange = { viewModel.currentPassword = it },
                isVisible = currentVisible,
                onToggleVisibility = { currentVisible = !currentVisible },
                error = currentPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Mật khẩu mới",
                password = newPassword,
                onPasswordChange = { viewModel.newPassword = it },
                isVisible = newVisible,
                onToggleVisibility = { newVisible = !newVisible },
                error = newPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Nhập lại mật khẩu mới",
                password = confirmPassword,
                onPasswordChange = { viewModel.confirmPassword = it },
                isVisible = confirmVisible,
                onToggleVisibility = { confirmVisible = !confirmVisible },
                error = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = Color(0xFF00C853),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = { viewModel.changePassword() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                enabled = !isLoading
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
            colors = TextFieldDefaults.colors(

                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                errorIndicatorColor = MaterialTheme.colorScheme.error,

                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White,
                errorContainerColor = White,

                // Other colors
                cursorColor = MaterialTheme.colorScheme.primary,

                // Ensure text color doesn't change when visually "disabled" (readOnly)
                disabledTextColor = LocalContentColor.current
            ),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        painter = painterResource(
                            id = if (isVisible) R.drawable.eye_1 else R.drawable.eye_off_1
                        ),
                        contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = if (isVisible) MainButton else LightGray
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
