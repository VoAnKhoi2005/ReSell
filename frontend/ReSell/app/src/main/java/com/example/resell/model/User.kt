package com.example.resell.model

import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val email: String? = null,
    @Json(name = "is_email_verified")
    val isEmailVerified: Boolean,
    val phone: String? = null,
    @Json(name = "is_phone_verified")
    val isPhoneVerified: Boolean,
    val password: String,
    @Json(name = "firebase_uid")
    val firebaseUID: String? = null,
    @Json(name = "auth_provider")
    val authProvider: String,
    @Json(name = "full_name")
    val fullName: String,
    val status: String,
    val reputation: Int,
    @Json(name = "ban_start")
    val banStart: LocalDateTime? = null,
    @Json(name = "ban_end")
    val banEnd: LocalDateTime? = null,
    @Json(name = "created_at")
    val createdAt: LocalDateTime,
    @Json(name = "is_selling")
    val isSelling: Boolean,
    @Json(name = "stripe_account_id")
    val stripeAccountID: String? = null,
    @Json(name = "is_stripe_verified")
    val isStripeVerified: Boolean,
    @Json(name = "avatar_url")
    val avatarURL: String? = null
)

@JsonClass(generateAdapter = true)
data class UserStatResponse(
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "username")
    val username: String? = null,
    @Json(name = "avatar_url")
    val avatarURL: String? = null,
    @Json(name = "cover_url")
    val coverURL: String? = null,
    @Json(name = "chat_response_percentage")
    val chatResponsePercentage: Float? = null,
    @Json(name = "reputation")
    val reputation: Int,
    @Json(name = "post_number")
    val postNumber: Int,
    @Json(name = "bought_number")
    val boughtNumber: Int,
    @Json(name = "sale_number")
    val saleNumber: Int,
    @Json(name = "report_number")
    val reportNumber: Int,
    @Json(name = "total_revenue")
    val totalRevenue: Int,
    @Json(name = "last_activity")
    val lastActivity: LocalDateTime? = null,
    @Json(name = "average_rating")
    val averageRating: Double,
    @Json(name = "review_number")
    val reviewNumber: Int,
    @Json(name = "average_post_price")
    val averagePostPrice: Double,
    @Json(name = "follower_count")
    val followerCount: Int,
    @Json(name = "followee_count")
    val followeeCount: Int,
    @Json(name = "created_at")
    val createAt: LocalDateTime,
)