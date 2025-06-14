package model

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review (
    val userId: String,
    val orderId: String,
    var rating: Int,
    var comment: String,
    val createdAt: LocalDateTime?,
)