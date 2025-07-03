package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.AddressPickerPopup
import com.example.resell.ui.components.OrderButton
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
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

            EditableBoxField(
                label = "Họ và tên",
                value = viewModel.fullName,
                onValueChange = {
                    viewModel.fullName = it
                    fullNameError = null
                },
                error = fullNameError
            )

            Spacer(modifier = Modifier.height(12.dp))

            EditableBoxField(
                label = "Số điện thoại",
                value = viewModel.phoneNumber,
                onValueChange = {
                    viewModel.phoneNumber = it
                    phoneError = null
                },
                error = phoneError
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoBoxField(
                label = "Khu vực",
                value = viewModel.getLocationString(),
                isError = locationError != null,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Mở danh sách")
                },
                onClick = { showPicker = true }
            )

            if (locationError != null) {
                Text(locationError!!, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }

            Spacer(modifier = Modifier.height(12.dp))

            EditableBoxField(
                label = "Địa chỉ chi tiết",
                value = viewModel.detail,
                onValueChange = {
                    viewModel.detail = it
                    detailError = null
                },
                error = detailError
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Đặt làm địa chỉ mặc định", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = viewModel.isDefault,
                    onCheckedChange = { viewModel.isDefault = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DarkBlue,
                        checkedTrackColor = DarkBlue.copy(alpha = 0.54f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                    )
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
            onAddressSelected = { _, _, _ ->
                pickerViewModel.selectedProvince?.let { p ->
                    pickerViewModel.selectedDistrict?.let { d ->
                        pickerViewModel.selectedWard?.let { w ->
                            viewModel.updateLocation(p, d, w)
                        }
                    }
                }
                showPicker = false
            },
            allowAll = false
        )
    }
}

@Composable
fun InfoBoxField(
    label: String,
    value: String,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val bgColor = if (isError)
        MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
    else
        GrayFont.copy(alpha = 0.3f)

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(OutlinedTextFieldDefaults.shape)
        .background(bgColor)
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }
        .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = GrayFont)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value.ifBlank { "Chưa chọn" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (value.isBlank()) GrayFont else Color.Black
                )
            )
            if (trailingIcon != null) trailingIcon()
        }
    }
}

@Composable
fun EditableBoxField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = error != null,
            modifier = Modifier.fillMaxWidth()
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

private fun validateInput(
    fullname: String,
    phone: String,
    detail: String,
    location: String,
    onError: (field: String, msg: String) -> Unit
): Boolean {
    var isValid = true

    if (fullname.trim().isEmpty()) {
        onError("fullname", "Họ và tên không được để trống")
        isValid = false
    }

    if (phone.trim().isEmpty()) {
        onError("phone", "Số điện thoại không được để trống")
        isValid = false
    } else if (!phone.matches(Regex("^0\\d{9}$"))) {
        onError("phone", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
        isValid = false
    }

    if (location.trim().isEmpty() || location == "Chưa chọn") {
        onError("location", "Vui lòng chọn khu vực")
        isValid = false
    }

    if (detail.trim().isEmpty()) {
        onError("detail", "Vui lòng nhập địa chỉ chi tiết")
        isValid = false
    }

    return isValid
}
