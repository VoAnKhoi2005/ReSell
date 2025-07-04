package com.example.resell.ui.screen.order.BuyingOrder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.ShopOrder
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.screen.home.ProductPost
import com.example.resell.ui.theme.CancelButton
import com.example.resell.ui.theme.ConfirmButton
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.order.MyOrder.MyOrderViewModel

@Composable
fun AllOrderScreen(
    viewModel: MyOrderViewModel = hiltViewModel()
) {
    // ✅ Lấy buyerId từ ReactiveStore (người dùng hiện tại)
    val currentUser = ReactiveStore<User>().item.collectAsState()
    val buyerId = currentUser.value?.id

    // ⏳ Load đơn khi có buyerId
    LaunchedEffect(buyerId) {
        buyerId?.let { viewModel.loadOrders(it) }
    }

    val isLoading = viewModel.isLoading
    val orders = viewModel.orders

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        orders.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có đơn hàng nào.")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(orders) { order ->
                    OrderItemWithActions(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItemWithActions(order: ShopOrder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Hiển thị thông tin cơ bản
            Text("Mã đơn: ${order.id}", style = MaterialTheme.typography.bodyMedium)
            Text("Tổng tiền: ${order.total} đ", style = MaterialTheme.typography.bodyMedium)
            Text("Trạng thái: ${order.status}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { /* TODO: Gửi yêu cầu xác nhận */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmButton),
                    modifier = Modifier
                        .height(36.dp)
                        .weight(1f)
                ) {
                    Text("Xác nhận", fontSize = 12.sp, color = White2)
                }

                Button(
                    onClick = { /* TODO: Gửi yêu cầu hủy */ },
                    colors = ButtonDefaults.buttonColors(containerColor = CancelButton),
                    modifier = Modifier
                        .height(36.dp)
                        .weight(1f)
                ) {
                    Text("Hủy", fontSize = 12.sp, color = LoginButton)
                }
            }
        }
    }
}




