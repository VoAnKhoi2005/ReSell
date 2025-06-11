package com.example.myapplication.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.ProductPostItem
import com.example.myapplication.ui.components.TopBar
import com.example.myapplication.ui.model.PostImage

@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        HomeContent(modifier = Modifier.padding(innerPadding))
    }
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
        )
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(postList) { post ->
            ProductPostItem(
                title = post.title,
                time = post.time,
                imageUrl = post.imageUrl,
                price = post.price,
                category = post.category,
                address = post.address
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

