package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id")
    val id: String,
    @Json(name = "user_id")
    val userId: String? = null,
    @Json(name = "category_id")
    val categoryId: String? = null,
    @Json(name = "address_id")
    val addressId: String? = null,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "price")
    val price: Int,
    @Json(name = "status")
    val status: PostStatus,
    @Json(name = "sold_at")
    val soldAt: LocalDateTime? = null,
    @Json(name = "created_at")
    val createdAt: LocalDateTime,
    @Json(name = "updated_at")
    val updatedAt: LocalDateTime,
    @Json(name = "deleted_at")
    val deletedAt: LocalDateTime? = null,
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
