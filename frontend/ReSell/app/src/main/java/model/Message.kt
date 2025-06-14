package model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val createdAt: LocalDateTime?,
    val avatarURL: String//tạm thời thêm
)
