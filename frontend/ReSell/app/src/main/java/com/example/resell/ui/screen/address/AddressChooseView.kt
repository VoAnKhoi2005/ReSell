package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.AddressBoxColor
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.address.AddressSetupViewModel

@Composable
fun AddressChooseScreen(
    viewModel: AddressSetupViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val addresses = viewModel.addressList
    val user by ReactiveStore<User>().item.collectAsState()

    val navController = NavigationController.navController
    val navBackStackEntry = remember {
        navController.currentBackStackEntry
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
                titleText = "Chọn địa chỉ nhận hàng",
                showBackButton = true,
                onBackClick = {
                    navController.popBackStack()
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
                        backgroundColor = AddressBoxColor,
                        onClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedAddressId", address.id)
                            navController.popBackStack()
                        }


                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}
