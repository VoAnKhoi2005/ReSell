package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

enum class NotificationType {
    @Json(name = "message") MESSAGE,
    @Json(name = "alert") ALERT,
    @Json(name = "system") SYSTEM,
    @Json(name = "order") ORDER,
    @Json(name = "default") DEFAULT
}

@JsonClass(generateAdapter = true)
data class Notification(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "topic") val topic: String? = null,
    @Json(name = "type") val type: NotificationType,
    @Json(name = "is_read") val isRead: Boolean,
    @Json(name = "is_sent") val isSent: Boolean,
    @Json(name = "is_silent") val isSilent: Boolean,
    @Json(name = "created_at") val createdAt: LocalDateTime? = null,
    @Json(name = "sent_at") val sentAt: LocalDateTime? = null
)
