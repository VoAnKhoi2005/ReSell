package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartItem (
    val cartId: String,
    val postId: String,
)