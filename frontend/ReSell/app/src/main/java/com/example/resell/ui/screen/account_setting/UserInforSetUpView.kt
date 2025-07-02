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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.theme.White2
import com.example.resell.ui.navigation.NavigationController
import java.text.SimpleDateFormat
import java.util.*
import com.example.resell.R
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.Red
import com.example.resell.ui.theme.White

@Composable
fun AccountSettingScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("example@gmail.com") } // Hoặc "" nếu chưa có
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Cài đặt tài khoản",
                showBackButton = true,
                onBackClick = {
                    NavigationController.navController.popBackStack()
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White2)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {
            // Họ và tên
            TitleWithRequired(text = "Họ và tên")
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Địa chỉ
            Text(text = "Địa chỉ của bạn")
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Số điện thoại
            TitleWithRequired(text = "Số điện thoại")
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(12.dp))

            // Mật khẩu
            TitleWithRequired(text = "Mật khẩu")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(12.dp))

            // Email nếu có
            Text(text = "Email")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO: Lưu thông tin cài đặt
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Lưu thay đổi")
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}


@Composable
fun TitleWithRequired(text: String) {
    Row {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = " *",
            color = Red
        )
    }
}
