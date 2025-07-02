package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class PaymentMethod(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "id")
    val id: String,

    @Json(name = "order_id")
    val orderId: String,

    @Json(name = "user_id")
    val userId: String,

    @Json(name = "stripe_payment_intent_id")
    val stripePaymentIntentId: String,

    @Json(name = "amount")
    val amount: Int,

    @Json(name = "status")
    val status: TransactionStatus,

    @Json(name = "payment_method_id")
    val paymentMethodId: String,

    @Json(name = "error_message")
    val errorMessage: String? = null,

    @Json(name = "created_at")
    val createdAt: OffsetDateTime,

    @Json(name = "order")
    val order: ShopOrder? = null,

    @Json(name = "user")
    val user: User? = null,

    @Json(name = "payment_method")
    val paymentMethod: PaymentMethod? = null
)

enum class TransactionStatus {
    @Json(name = "pending")
    PENDING,

    @Json(name = "completed")
    COMPLETED,

    @Json(name = "failed")
    FAILED
}