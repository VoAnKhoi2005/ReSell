package com.example.resell.ui.screen.order.BuyingOrder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.screen.home.ProductPost
import com.example.resell.ui.theme.CancelButton
import com.example.resell.ui.theme.ConfirmButton
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2

@Composable
fun AllOrderScreen(){
    val postList = listOf(
        ProductPost(
            id="",
            title = "iPhone 14 Pro Max 256GB",
            time = "2 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?q=80",
            price = 25500000,
            category = "Điện thoại",
            address = "Quận 1, TP.HCM"
        ),
        ProductPost(
            id="",
            title = "Xe máy Honda SH 2023",
            time = "5 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?q=80",
            price = 75000000,
            category = "Phương tiện",
            address = "Quận 7, TP.HCM"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp)
    ) {
        items(postList) { post ->
            OrderItemWithActions(post)
        }
    }
}
@Composable
fun OrderItemWithActions(post: ProductPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            // ✅ Nội dung sản phẩm
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                ProductPostItemHorizontalImage(
                    title = post.title,
                    time = post.time,
                    imageUrl = post.imageUrl,
                    price = post.price,
                    address = post.address
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ✅ Hai nút dọc bên phải
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Button(
                    onClick = { /* TODO: Xác nhận */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmButton),
                    modifier = Modifier
                        .height(36.dp)
                        .width(IntrinsicSize.Max)
                ) {
                    Text("Xác nhận", fontSize = 12.sp, style = MaterialTheme.typography.labelMedium , color = White2, maxLines = 1)
                }

                Button(
                    onClick = { /* TODO: Hủy */ },
                    colors = ButtonDefaults.buttonColors(containerColor = CancelButton),
                    modifier = Modifier
                        .height(36.dp)
                        .width(IntrinsicSize.Max)
                ) {
                    Text("Hủy", fontSize = 12.sp,style = MaterialTheme.typography.labelMedium, color = LoginButton, maxLines =1 )
                }
            }
        }
    }
}



