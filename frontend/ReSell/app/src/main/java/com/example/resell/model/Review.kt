package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "user_id") val userId: String,
    @Json(name = "order_id") val orderId: String,
    @Json(name = "rating") var rating: Int,
    @Json(name = "comment") var comment: String,
    @Json(name = "created_at") val createdAt: LocalDateTime?
)