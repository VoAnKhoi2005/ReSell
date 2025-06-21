package com.example.resell.model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Conversation(
    val id: String,
    val buyerId: String,
    val sellerId: String,
    val postId: String,
    val createdAt: LocalDateTime?,
    val messages: List<Message>
)
