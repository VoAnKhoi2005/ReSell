package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Follow(
    @Json(name = "follower_id") val followerId: String,
    @Json(name = "followee_id") val followeeId: String
)