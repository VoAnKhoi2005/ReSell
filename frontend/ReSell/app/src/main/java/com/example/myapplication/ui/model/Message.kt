package com.example.myapplication.ui.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val createdAt: LocalDateTime?
)
