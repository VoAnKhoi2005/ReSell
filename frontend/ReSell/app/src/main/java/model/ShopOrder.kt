package model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

enum class OrderStatus{
    pending, processing, shipping, completed, cancelled
}

@JsonClass(generateAdapter = true)
data class ShopOrder(
    val id: String,
    val userId: String,
    val postId: String,
    var status: String,
    var addressId: String,
    var total: Int,
    val createdAt: LocalDateTime?,
    var completedAt:LocalDateTime?,
    var canceledAt: LocalDateTime?,
    val review: Review?
)
