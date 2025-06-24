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

