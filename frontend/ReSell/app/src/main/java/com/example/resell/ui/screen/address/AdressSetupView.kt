package com.example.resell.ui.screen.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.payment.OrderButton
import com.example.resell.ui.theme.White2

data class AddressInfo(
    val name: String,
    val phone: String,
    val address: String,
    val default: Boolean=false
)
@Composable
fun AddressSetupScreen() {
    val scrollState = rememberScrollState()

    val sampleAddresses = listOf(
        AddressInfo("Phạm Thành Long", "08366333080", "123 Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh",true),
        AddressInfo("Nguyễn Văn A", "0901234567", "456 Trần Hưng Đạo, Quận 5, TP. Hồ Chí Minh"),
        AddressInfo("Trần Thị B", "0987654321", "789 Nguyễn Trãi, Quận 10, TP. Hồ Chí Minh")
    )

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
            sampleAddresses.forEach { info ->
                AddressBox(
                    receiverName = info.name,
                    phoneNumber = info.phone,
                    address = info.address,
                    showIcon = info.default,
                    onClick = {
                        // TODO: xử lý chọn địa chỉ
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))
            }
            OrderButton(
                text = "Thêm địa chỉ mới",
                onClick = {
                    NavigationController.navController.navigate(Screen.AddressAdd.route)
                }
            )
        }
    }
}

