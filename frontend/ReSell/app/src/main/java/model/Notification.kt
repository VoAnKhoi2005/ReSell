package model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

enum class NotificationType{
    message, alert, system, reminder
}

@JsonClass(generateAdapter = true)
data class Notification (
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val topic: String? = null,
    val type: NotificationType,
    val isRead: Boolean,
    val isSent: Boolean,
    val isSilent: Boolean,
    val createdAt: LocalDateTime? = null,
    val sentAt: LocalDateTime? = null,
)