package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id") val id: String,
    @Json(name = "owner") val userId: String,
    @Json(name = "category") val category: String,
    @Json(name = "title") val title: String,
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "province") val address: String,
    @Json(name = "price") val price: Int,
    @Json(name = "status") val status: String,
    @Json(name = "created_at") val createdAt: LocalDateTime? = null,
    @Json(name = "sold_at") val soldAt: LocalDateTime? = null,
    @Json(name = "updated_at") val updatedAt: LocalDateTime? = null,
    @Json(name = "deleted_at") val deletedAt: LocalDateTime? = null,
    @Json(name = "conversations") val conversations: List<Conversation> = emptyList(),
    @Json(name = "order") val order: ShopOrder? = null,
    @Json(name = "images") val images: List<PostImage> = emptyList()

)

@JsonClass(generateAdapter = false)
enum class PostStatus {
    @Json(name = "pending")
    PENDING,

    @Json(name = "approved")
    APPROVED,

    @Json(name = "rejected")
    REJECTED,

    @Json(name = "sold")
    SOLD,

    @Json(name = "hidden")
    HIDDEN,

    @Json(name = "deleted")
    DELETED
}
