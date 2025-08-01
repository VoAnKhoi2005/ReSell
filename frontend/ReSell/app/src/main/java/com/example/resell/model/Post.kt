package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userID: String,
    @Json(name = "category_id") val categoryID: String,
    @Json(name = "ward_id") val wardID: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "price") val price: Int,
    @Json(name = "status") val status: PostStatus,
    @Json(name = "created_at") val createdAt: LocalDateTime? = null,
    @Json(name = "updated_at") val updatedAt: LocalDateTime? = null,
    @Json(name = "deleted_at") val deletedAt: LocalDateTime? = null,
    @Json(name = "sold_at") val soldAt: LocalDateTime? = null,
    @Json(name = "user") val user: User? = null,
    @Json(name = "category") val category: Category? = null,
    @Json(name = "ward") val ward: Ward? = null,
    @Json(name = "post_images") val images: List<PostImage>? = null
)

@JsonClass(generateAdapter = true)
data class PostData(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "status") val status: String,
    @Json(name = "category") val category: String,
    @Json(name = "owner") val owner: String,
    @Json(name = "fullname") val fullname: String,
    @Json(name="user_id") val userId:String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "description") val description: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "address") val address: String,
    @Json(name = "price") val price: Int,
    @Json(name = "created_at") val createdAt: LocalDateTime?=null,
    @Json(name = "is_following") val isFollowing: Boolean,
    @Json(name = "is_favorite") val isFavorite: Boolean,
    @Json(name = "images") var images: List<PostImage>? = emptyList()
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
    SOLD
}