package com.example.resell.ui.screen.postmanagement

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.screen.home.ProductPost

@Composable
fun NotApprovedScreen(isCurrentUser: Boolean){
    val postList = listOf(
        ProductPost(
            id="",
            title = "iPhone 14 Pro Max 256GB",
            time = "2 giờ trước",
            imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?q=80",
            price = 25500000,
            category = "Điện thoại",
            address = "Quận 1, TP.HCM"
        )
    )
    if(isCurrentUser){
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
    else{
        Text(text = "Không có bài đăng nào")
    }
}