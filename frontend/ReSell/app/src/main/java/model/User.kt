package model

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val fullName: String,
    val citizenId: String,
    val birthday: LocalDate,
    val gender: Boolean,
    val status: String,
    val reputation: Int,
    val banStart: LocalDateTime?,
    val banEnd: LocalDateTime?,
    val createdAt: LocalDateTime
)

