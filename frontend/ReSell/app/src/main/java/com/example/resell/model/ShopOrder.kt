package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

enum class OrderStatus {
    @Json(name = "pending")
    PENDING,

    @Json(name = "processing")
    PROCESSING,

    @Json(name = "shipping")
    SHIPPING,

    @Json(name = "completed")
    COMPLETED,

    @Json(name = "cancelled")
    CANCELLED
}

@JsonClass(generateAdapter = true)
data class ShopOrder(
    val id: String,
    val userId: String,
    val postId: String,
    var status: OrderStatus,
    var addressId: String,
    var total: Int,
    val createdAt: LocalDateTime?,
    var completedAt:LocalDateTime?,
    var canceledAt: LocalDateTime?,
    val review: Review?
)
