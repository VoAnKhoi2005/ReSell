package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SubscriptionPlan(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "duration")
    val duration: Int,

    @Json(name = "stripe_price_id")
    val stripePriceId: String
)

@JsonClass(generateAdapter = true)
data class UserSubscription(
    @Json(name = "id")
    val id: String,

    @Json(name = "user_id")
    val userId: String,

    @Json(name = "subscription_id")
    val planId: String,

    @Json(name = "start_at")
    val startAt: OffsetDateTime,

    @Json(name = "end_at")
    val endAt: OffsetDateTime,

    @Json(name = "is_active")
    val isActive: Boolean,

    @Json(name = "stripe_subscription_id")
    val stripeSubscriptionId: String,

    @Json(name = "user")
    val user: User? = null,

    @Json(name = "plan")
    val plan: SubscriptionPlan? = null
)
