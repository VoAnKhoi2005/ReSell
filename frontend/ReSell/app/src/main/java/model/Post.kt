package model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val id: String,
    val userId: String,
    var categoryId: String,
    var title: String,
    var description: String,
    var addressId: String,
    var price: Int,
    var status: String,
    val createdAt: LocalDateTime?,
    var soldAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?,
    val conversations: List<Conversation>,
    val order: ShopOrder?,
    val images: List<PostImage>
)
