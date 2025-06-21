package com.example.resell.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthToken (
    val accessToken: String,
    val refreshToken: String
)