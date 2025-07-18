package com.example.resell.ui.screen.payment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.ui.components.AddressBox
import com.example.resell.ui.components.OrderButton
import com.example.resell.ui.components.PaymentMethodSelector
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.components.TotalAmountBox
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.payment.PaymentViewModel
import com.example.resell.util.getRelativeTime


@Composable
fun PaymenContentScreen(
     viewModel: PaymentViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val navBackStackEntry = NavigationController.navController.currentBackStackEntry
    val selectedId = navBackStackEntry?.savedStateHandle?.get<String>("selectedAddressId")
    val alreadyFetched = remember { mutableStateOf(false) }
    val post by viewModel.postFlow.collectAsState()
    val conversation by viewModel.conversationFlow.collectAsState()
    LaunchedEffect(selectedId) {
        if (!selectedId.isNullOrBlank() && !alreadyFetched.value) {
            viewModel.fetchAddressById(selectedId)
            alreadyFetched.value = true
        }
    }

    val receiverName = selectedAddress?.fullname ?: "Chưa có tên"
    val phoneNumber = selectedAddress?.phone ?: "Chưa có số điện thoại"
    val address = selectedAddress?.let {
        listOfNotNull(
            it.detail,
            it.ward?.name,
            it.ward?.district?.name,
            it.ward?.district?.province?.name
        ).joinToString(", ")
    } ?: "Chưa chọn địa chỉ"

    val paymentMethods = listOf(
        "Thanh toán khi nhận hàng (COD)",
        "ZaloPay"
    )
    val selectedMethodIndex by viewModel.selectedPaymentMethod.collectAsState()


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
                enable = selectedAddress!=null,
                onClick = {
                    when (viewModel.selectedPaymentMethod.value) {
                        0 -> viewModel.orderWithCOD()
                        1 -> viewModel.orderWithZaloPay()
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
                        NavigationController.navController.navigate(Screen.AddressChooseScreen.route)
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
                            title = post!!.title,
                            time = getRelativeTime(post!!.createdAt),
                            imageUrl = post!!.images?.get(0)?.url?:"",
                            price = conversation!!.offer!!,
                            address = post!!.ward?.name?:"",
                            showExtraInfo = false
                        )
                        Divider(color = LightGray, thickness = 0.5.dp)
                        TotalAmountBox(totalAmount = conversation!!.offer!!)
                        PaymentMethodSelector(
                            methods = paymentMethods,
                            selectedIndex = selectedMethodIndex,
                            onSelect = { viewModel.selectPaymentMethod(it) }

                        )
                    }
                }
            }
        }
    }
}






