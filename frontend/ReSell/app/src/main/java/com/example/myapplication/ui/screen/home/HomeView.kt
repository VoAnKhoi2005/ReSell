package com.example.myapplication.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import com.example.myapplication.R
import com.example.myapplication.ui.components.ProductPostItem
import com.example.myapplication.ui.components.TopBar
import com.example.myapplication.ui.model.PostImage

@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) { HomeContent() }

}

@Composable
fun HomeContent(modifier: Modifier = Modifier) {
    val postList = listOf(
        ProductPost(
            title = "iPhone 14 Pro Max 256GB",
            time = "2 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 25500000,
            category = "Điện thoại",
            address = "Quận 1, TP.HCM"
        ),
        ProductPost(
            title = "Xe máy Honda SH 2023",
            time = "5 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?q=80&w=1960&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 75000000,
            category = "Phương tiện",
            address = "Quận 7, TP.HCM"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 cột
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .drawBehind{
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Tin đăng dành cho bạn",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alignByBaseline()

                )
                Spacer(modifier = Modifier.padding(2.dp))
                Image(
                    painter = painterResource(id = R.drawable.tuychinhdanhmucbtn),
                    contentDescription = "Gợi ý",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            // 👉 Xử lý khi click vào icon ở đây
                            println("Icon được click")
                        }
                        .padding(bottom = 16.dp)
                )
            }
        }
        items(postList) { post ->
            ProductPostItem(
                title = post.title,
                time = post.time,
                imageUrl = post.imageUrl,
                price = post.price,
                category = post.category,
                address = post.address,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
data class ProductPost(//test
    val title: String,
    val time: String,
    val imageUrl: String,
    val price: Int,
    val category: String,
    val address: String
)

