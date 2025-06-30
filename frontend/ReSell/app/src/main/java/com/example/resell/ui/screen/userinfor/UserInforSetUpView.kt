package com.example.resell.ui.screen.userinfor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun AccountSettingScreen() {//ho ten, dia chi, so dien thoai, mat khau(hien view doi mk),email(co thi hien, k thi thoi)

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Gender Dropdown
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Nam", "Nữ", "Ẩn")
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }

    // Date of Birth
    val calendar = Calendar.getInstance()

    var dob by remember { mutableStateOf("") }


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
            TitleWithRequired(text = "Họ và tên")
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text(text = "Địa chỉ của bạn")
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            TitleWithRequired(text = "Số điện thoại")
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(12.dp))
            Text(text = "Giới thiệu")
            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(12.dp))
            Text(text = "Email")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {}, // tránh chiếm gesture
                    label = { Text("Giới tính") },
                    trailingIcon = {
                        IconButton(onClick = { genderExpanded = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown_icon),
                                contentDescription = "Mở menu"
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { genderExpanded = true }
                )

                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false },
                    modifier = Modifier.fillMaxWidth().background(White),

                ) {
                    genderOptions.forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender) },
                            onClick = {
                                selectedGender = gender
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(text = "Ngày sinh")
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val today = sdf.format(Date())
                        dob = today
                        // Bạn có thể tích hợp DatePicker nếu cần chi tiết hơn
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar_add_duotone),
                            contentDescription = "Chọn ngày",
                            Modifier.size(20.dp)
                        )
                    }
                },
                readOnly = true
            )

            Spacer(Modifier.height(12.dp))
            Text(text = "CCCD / CMND / Hộ chiếu")
            OutlinedTextField(
                value = idNumber,
                onValueChange = { idNumber = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            TitleWithRequired(text = "Mật khẩu")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(12.dp))
            Text(text = "Tên người dùng")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth()
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
