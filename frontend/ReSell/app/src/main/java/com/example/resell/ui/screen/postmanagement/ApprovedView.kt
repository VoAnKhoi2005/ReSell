package com.example.resell.ui.screen.postmanagement

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.screen.home.ProductPost
import androidx.compose.foundation.lazy.items
@Composable
fun ApproveScreen() {
    val postList = listOf(
        ProductPost(
            title = "iPhone 14 Pro Max 256GB",
            time = "2 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?q=80",
            price = 25500000,
            category = "Điện thoại",
            address = "Quận 1, TP.HCM"
        ),
        ProductPost(
            title = "Xe máy Honda SH 2023",
            time = "5 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?q=80",
            price = 75000000,
            category = "Phương tiện",
            address = "Quận 7, TP.HCM"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        ),
        ProductPost(
            title = "MacBook Pro M1 2020",
            time = "1 ngày trước",
            imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80",
            price = 32000000,
            category = "Laptop",
            address = "Hà Nội"
        )
    )

    LazyColumn {
        items(postList) { post ->
            ProductPostItemHorizontalImage(
                title = post.title,
                time = post.time,
                imageUrl = post.imageUrl,
                price = post.price,
                address = post.address
            )
        }
    }
}
