package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "category_id") val categoryId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "address_id") val addressId: String,
    @Json(name = "price") val price: Int,
    @Json(name = "status") val status: String,

    @Json(name = "images") val images: List<PostImage> = emptyList()

)
@JsonClass(generateAdapter = true)
data class PostListItem(
    @Json(name = "id") val id: String,
    @Json(name = "owner") val owner: String,
    @Json(name = "category") val category: String,
    @Json(name = "title") val title: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "province") val address: String,
    @Json(name = "price") val price: Int,
    @Json(name = "status") val status: String,
    @Json(name = "created_at") val createdAt: LocalDateTime? = null
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
