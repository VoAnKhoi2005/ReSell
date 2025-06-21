package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.payment.OrderButton
import com.example.resell.ui.theme.White2

@Composable
fun AddressAddScreen() {
    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }

    var detail by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Địa chỉ mới",
                showBackButton = true,
                onBackClick = { NavigationController.navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White2)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {

            Text(
                text = "Thông tin địa chỉ",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProvinceDropdownField(
                selectedProvince = province,
                onClick = {
                    NavigationController.navController.navigate(Screen.ProvinceSelect.route)
                }
            )


            Spacer(modifier = Modifier.height(12.dp))

           DistrictDropdownField(
               selectedDistrict = district,
               onClick = {  NavigationController.navController.navigate(Screen.ProvinceSelect.route)}
           )

            Spacer(modifier = Modifier.height(12.dp))

           WardDropdownField(
               selectedWard = ward,
               onClick = {}
           )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = detail,
                onValueChange = { detail = it },
                label = { Text("Địa chỉ chi tiết") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Đặt làm địa chỉ mặc định",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OrderButton(
                text = "Lưu địa chỉ",
                onClick = {
                }
            )
        }
    }
}
@Composable
fun ProvinceDropdownField(
    selectedProvince: String?,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = selectedProvince ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tỉnh/Thành phố") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở danh sách"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun DistrictDropdownField(
    selectedDistrict: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = selectedDistrict ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Quận/Huyện") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở danh sách"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WardDropdownField(
    selectedWard: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = selectedWard ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Phường/Xã") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở danh sách"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}




