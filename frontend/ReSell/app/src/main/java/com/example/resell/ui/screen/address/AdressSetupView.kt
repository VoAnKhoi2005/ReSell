package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.OrderButton
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen

import com.example.resell.ui.theme.AddressBoxColor
import com.example.resell.ui.theme.Red
import com.example.resell.ui.theme.SelectToDeleteColor
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.theme.Yellow
import com.example.resell.ui.viewmodel.address.AddressSetupViewModel

@Composable
fun AddressSetupScreen(
    viewModel: AddressSetupViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val addresses = viewModel.addressList
    val selectedID = viewModel.selectedAddressID
    val user by ReactiveStore<User>().item.collectAsState()

    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedAddressIds = remember { mutableStateListOf<String>() }

    val navBackStackEntry = remember {
        NavigationController.navController.currentBackStackEntry
    }
    val shouldReload = navBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("shouldReload", false)
        ?.collectAsState()

    LaunchedEffect(shouldReload?.value) {
        if (shouldReload?.value == true) {
            viewModel.fetchAddresses()
            navBackStackEntry.savedStateHandle["shouldReload"] = false
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Thiết lập địa chỉ",
                showBackButton = true,
                onBackClick = {
                    NavigationController.navController.popBackStack()
                },
                actions = {
                    TextButton(onClick = {
                        isDeleteMode = !isDeleteMode
                        if (!isDeleteMode) selectedAddressIds.clear()
                    }) {
                        Text(
                            text = if (isDeleteMode) "Hủy" else "Xóa",
                            color = if (isDeleteMode) Red else White,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 20.sp)
                        )
                    }
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
                .padding(12.dp)
        ) {
            addresses
                .sortedByDescending { it.isDefault }
                .forEach { address ->
                val isSelected = selectedAddressIds.contains(address.id)
                    val backgroundColor = if (isDeleteMode && isSelected) SelectToDeleteColor else AddressBoxColor

                    AddressBox(
                        receiverName = address?.fullname ?: "Người dùng",
                        phoneNumber = address?.phone ?: "",
                        address = listOfNotNull(
                            address.detail,
                            address.ward?.name,
                            address.ward?.district?.name,
                            address.ward?.district?.province?.name
                        ).joinToString(", "),
                        showIcon = address.isDefault,
                        backgroundColor = backgroundColor, // TRUYỀN VÀO ĐÂY
                        onClick = {
                            if (isDeleteMode) {
                                if (selectedAddressIds.contains(address.id)) {
                                    selectedAddressIds.remove(address.id)
                                } else {
                                    selectedAddressIds.add(address.id)
                                }
                            } else {
                                NavigationController.navController.navigate(Screen.AddressAdd.route + "?id=${address.id}")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
            }

            if (isDeleteMode && selectedAddressIds.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.deleteAddresses(
                            selectedAddressIds.toList(),
                            onSuccess = {
                                selectedAddressIds.clear()
                                isDeleteMode = false
                            }
                        )
                    }
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Xóa ${selectedAddressIds.size} địa chỉ")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isDeleteMode) {
                OrderButton(
                    text = "Thêm địa chỉ mới",
                    onClick = {
                        NavigationController.navController.navigate(Screen.AddressAdd.route)
                    }
                )
            }
        }
    }
}
