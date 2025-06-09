package com.example.myapplication.ui.model

import java.time.LocalDateTime

data class Review (
    val userId: String,
    val orderId: String,
    var rating: Int,
    var comment: String,
    val createdAt: LocalDateTime?,
)