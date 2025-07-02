package com.example.resell.ui.screen.account_setting

import androidx.compose.foundation.background
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

@Composable
fun AccountSettingScreen(
    viewModel: AccountSettingViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val user by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }

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
                onValueChange = { fullName = it }
            )


            InputWithLabel(
                label = "Số điện thoại",
                value = phone,
                onValueChange = { phone = it },
                keyboardType = KeyboardType.Phone,
                enabled = isPhoneEditable
            )

            if (!isPhoneEditable) {
                Text(
                    text = "Bạn không thể sửa số điện thoại sau khi đã xác minh.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            } else {
                Spacer(Modifier.height(12.dp))
            }

            InputWithLabel(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email,
                enabled = isEmailEditable
            )
            if (!isEmailEditable) {
                Text(
                    text = "Bạn không thể sửa email sau khi đã xác minh.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            } else {
                Spacer(Modifier.height(12.dp))
            }



            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveChanges(
                        name = fullName,
                        phone = phone,
                        email = email
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Lưu thay đổi", color = White)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun InputWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        enabled = enabled
    )
    Spacer(modifier = Modifier.height(12.dp))
}
