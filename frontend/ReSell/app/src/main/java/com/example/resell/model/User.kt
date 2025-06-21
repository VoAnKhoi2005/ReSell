package com.example.resell.model

import java.time.LocalDate
import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val email: String,
    val isEmailVerified: Boolean,
    val phone: String,
    val isPhoneVerified: Boolean,
    val password: String,
    val firebaseUID: String? = null,
    val authProvider: String,
    val fullName: String,
    val citizenId: String,
    val birthday: LocalDate,
    val gender: Boolean,
    val status: String,
    val reputation: Int,
    val banStart: LocalDateTime? = null,
    val banEnd: LocalDateTime? = null,
    val createdAt: LocalDateTime
)

