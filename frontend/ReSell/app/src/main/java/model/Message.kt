package model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val createdAt: LocalDateTime?,
)
