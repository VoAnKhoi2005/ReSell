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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
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
                onValueChange = { viewModel.fullName = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = { viewModel.phoneNumber = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true }
            ) {
                OutlinedTextField(
                    value = viewModel.getLocationString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Khu vực") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Mở danh sách"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.detail,
                onValueChange = { viewModel.detail = it },
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
                    checked = viewModel.isDefault,
                    onCheckedChange = { viewModel.isDefault = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OrderButton(
                text = if (viewModel.isEditMode) "Cập nhật địa chỉ" else "Lưu địa chỉ",
                onClick = {
                    viewModel.saveAddress {
                        NavigationController.navController.popBackStack()
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
