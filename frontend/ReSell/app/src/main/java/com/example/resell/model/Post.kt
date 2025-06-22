package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "category_id") var categoryId: String,
    @Json(name = "title") var title: String,
    @Json(name = "description") var description: String,
    @Json(name = "address_id") var addressId: String,
    @Json(name = "price") var price: Int,
    @Json(name = "status") var status: String,
    @Json(name = "created_at") val createdAt: LocalDateTime?,
    @Json(name = "sold_at") var soldAt: LocalDateTime?,
    @Json(name = "updated_at") var updatedAt: LocalDateTime?,
    @Json(name = "deleted_at") var deletedAt: LocalDateTime?,
    @Json(name = "conversations") val conversations: List<Conversation>,
    @Json(name = "order") val order: ShopOrder?,
    @Json(name = "images") val images: List<PostImage>
)
