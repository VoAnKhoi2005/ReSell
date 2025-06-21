package com.example.resell.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Follow (
    val followerId: String,
    val followeeId: String
)