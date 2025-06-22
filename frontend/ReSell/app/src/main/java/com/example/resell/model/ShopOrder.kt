package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

enum class OrderStatus {
    @Json(name = "pending") PENDING,
    @Json(name = "processing") PROCESSING,
    @Json(name = "shipping") SHIPPING,
    @Json(name = "completed") COMPLETED,
    @Json(name = "cancelled") CANCELLED
}


@JsonClass(generateAdapter = true)
data class ShopOrder(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "post_id") val postId: String,
    @Json(name = "status") var status: OrderStatus,
    @Json(name = "address_id") var addressId: String,
    @Json(name = "total") var total: Int,
    @Json(name = "created_at") val createdAt: LocalDateTime?,
    @Json(name = "completed_at") var completedAt: LocalDateTime?,
    @Json(name = "canceled_at") var canceledAt: LocalDateTime?,
    @Json(name = "review") val review: Review?
)

