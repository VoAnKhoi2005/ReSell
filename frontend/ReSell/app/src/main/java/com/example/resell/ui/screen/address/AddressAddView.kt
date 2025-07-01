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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.AddressPickerPopup
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.screen.payment.OrderButton
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.address.AddressAddViewModel
import com.example.resell.ui.viewmodel.components.AddressPickerViewModel

@Composable
fun AddressAddScreen(
    viewModel: AddressAddViewModel = hiltViewModel(),
    pickerViewModel: AddressPickerViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val user by ReactiveStore<User>().item.collectAsState()
    var showPicker by remember { mutableStateOf(false) }

    // Các biến lỗi cho từng trường
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var detailError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = if (viewModel.isEditMode) "Chỉnh sửa địa chỉ" else "Địa chỉ mới",
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
                value = viewModel.fullName,
                onValueChange = {
                    viewModel.fullName = it
                    fullNameError = null
                },
                label = { Text("Họ và tên") },
                isError = fullNameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (fullNameError != null) {
                Text(fullNameError!!, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = {
                    viewModel.phoneNumber = it
                    phoneError = null
                },
                label = { Text("Số điện thoại") },
                isError = phoneError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null) {
                Text(phoneError!!, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.getLocationString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Khu vực") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Mở danh sách")
                },
                isError = locationError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true }
            )
            if (locationError != null) {
                Text(locationError!!, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.detail,
                onValueChange = {
                    viewModel.detail = it
                    detailError = null
                },
                label = { Text("Địa chỉ chi tiết") },
                isError = detailError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (detailError != null) {
                Text(detailError!!, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Đặt làm địa chỉ mặc định", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = viewModel.isDefault,
                    onCheckedChange = { viewModel.isDefault = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OrderButton(
                text = if (viewModel.isEditMode) "Cập nhật địa chỉ" else "Lưu địa chỉ",
                onClick = {
                    val valid = validateInput(
                        viewModel.fullName,
                        viewModel.phoneNumber,
                        viewModel.detail,
                        viewModel.getLocationString(),
                        onError = { field, msg ->
                            when (field) {
                                "fullname" -> fullNameError = msg
                                "phone" -> phoneError = msg
                                "detail" -> detailError = msg
                                "location" -> locationError = msg
                            }
                        }
                    )

                    if (valid) {
                        viewModel.saveAddress {
                            NavigationController.navController.previousBackStackEntry
                                ?.savedStateHandle?.set("shouldReload", true)
                            NavigationController.navController.popBackStack()
                        }
                    }
                }
            )
        }
    }

    if (showPicker) {
        AddressPickerPopup(
            viewModel = pickerViewModel,
            onDismiss = { showPicker = false },
            onAddressSelected = { province, district, ward ->
                val selectedProvince = pickerViewModel.selectedProvince
                val selectedDistrict = pickerViewModel.selectedDistrict
                val selectedWard = pickerViewModel.selectedWard

                if (selectedProvince != null && selectedDistrict != null && selectedWard != null) {
                    viewModel.updateLocation(selectedProvince, selectedDistrict, selectedWard)
                }
                showPicker = false
            },
            allowAll = false
        )
    }
}

private fun validateInput(
    fullname: String,
    phone: String,
    detail: String,
    location: String,
    onError: (field: String, msg: String) -> Unit
): Boolean {
    var isValid = true

    if (fullname.isBlank()) {
        onError("fullname", "Họ và tên không được để trống")
        isValid = false
    }

    if (phone.isBlank()) {
        onError("phone", "Số điện thoại không được để trống")
        isValid = false
    } else if (!phone.matches(Regex("^0\\d{9}$"))) {
        onError("phone", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
        isValid = false
    }

    if (location.isBlank()) {
        onError("location", "Vui lòng chọn khu vực")
        isValid = false
    }

    if (detail.isBlank()) {
        onError("detail", "Vui lòng nhập địa chỉ chi tiết")
        isValid = false
    }

    return isValid
}
