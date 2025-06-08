package com.example.myapplication.ui.model

import java.time.LocalDateTime

data class ShopOrder(
    val id: String,
    val userId: String,
    val postId: String,
    var status: String,
    var addressId: String,
    var total: Int,
    val createdAt: LocalDateTime?,
    var completedAt:LocalDateTime?,
    var canceledAt: LocalDateTime?
)
