package model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notification (
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime?,
    var isRead: Boolean
)