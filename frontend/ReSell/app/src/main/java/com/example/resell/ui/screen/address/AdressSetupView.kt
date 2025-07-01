package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.payment.OrderButton
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.address.AddressSetupViewModel

@Composable
fun AddressSetupScreen(
    viewModel: AddressSetupViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val addresses = viewModel.addressList
    val selectedID = viewModel.selectedAddressID
    val user by ReactiveStore<User>().item.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Thiết lập địa chỉ",
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
            addresses.forEach { address ->
                AddressBox(
                    receiverName = user?.fullName ?: "Người dùng",
                    phoneNumber = user?.phone ?: "",
                    address = listOfNotNull(
                        address.detail,
                        address.ward?.name,
                        address.ward?.district?.name,
                        address.ward?.district?.province?.name
                    ).joinToString(", "),
                    showIcon = address.isDefault,
                    onClick = {
                        // 👉 Chuyển sang AddressAddScreen để sửa
                        NavigationController.navController.navigate(
                            Screen.AddressAdd.route + "?id=${address.id}"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OrderButton(
                text = "Thêm địa chỉ mới",
                onClick = {
                    // 👉 Thêm mới => không truyền ID
                    NavigationController.navController.navigate(Screen.AddressAdd.route)
                }
            )
        }
    }
}
