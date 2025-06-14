package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Cart (
    val id: String,
    val userId: String,
    val cartItems: List<CartItem>
)