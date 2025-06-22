package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartItem(
    @Json(name = "cart_id") val cartId: String,
    @Json(name = "post_id") val postId: String
)
