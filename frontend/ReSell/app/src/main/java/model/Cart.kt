package model

data class Cart (
    val id: String,
    val userId: String,
    val cartItems: List<CartItem>
)