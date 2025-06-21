package com.example.resell.ui.screen.payment

import android.R.attr.thickness
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.AdressBox
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.theme.priceColor


@Composable
fun PaymentScreen() {
    val scrollState = rememberScrollState()

    // Giả lập dữ liệu người nhận
    val receiverName = "Phạm Thành Long"
    val phoneNumber = "08366333080"
    val address = "123 Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh"

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Thanh toán",
                showBackButton = true,
                onBackClick = {
                    NavigationController.navController.popBackStack()
                }
            )
        },
        bottomBar = {
            OrderButton(
                text = "Đặt hàng",
                onClick = {
                    // TODO: xử lý đặt hàng
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White2)
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(12.dp)
            ) {

                AddressBox(
                    receiverName = receiverName,
                    phoneNumber = phoneNumber,
                    address = address,
                    onClick = {
                       NavigationController.navController.navigate(Screen.AddressSetup.route)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Thông tin thanh toán",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column {
                        ProductPostItemHorizontalImage(
                            title = "CatPlushieee",
                            time = "2 giờ trước",
                            imageUrl = "https://i.pinimg.com/736x/1c/63/8f/1c638ff41962012e45d77018a7be935c.jpg",
                            price = 25000000,
                            address = "Quận 1, TP.HCM",
                            showExtraInfo = false
                        )
                        Divider(
                            color = LightGray,
                            thickness = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TotalAmountBox(totalAmount = 25000000)
                    }
                }

                // TODO: thêm danh sách sản phẩm, voucher, tổng tiền,...
            }
        }
    }
}

@Composable
fun TotalAmountBox(totalAmount: Int) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng thanh toán",
                style = MaterialTheme.typography.labelMedium.copy( fontSize = 16.sp),
                color = LoginButton
            )
            Text(
                text = "%,d₫".format(totalAmount),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = priceColor
            )
        }
    }
@Composable
fun OrderButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
            ,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = White)
        )
    }
}



