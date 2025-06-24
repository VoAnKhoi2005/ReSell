package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Conversation(
    @Json(name = "id") val id: String,
    @Json(name = "buyer_id") val buyerId: String,
    @Json(name = "seller_id") val sellerId: String,
    @Json(name = "post_id") val postId: String,
    @Json(name = "created_at") val createdAt: LocalDateTime?,
    @Json(name = "buyer") val buyer: User? = null,
    @Json(name = "seller") val seller: User? = null,
    @Json(name = "post") val post: Post? = null
)

