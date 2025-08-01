package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthToken(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String
)