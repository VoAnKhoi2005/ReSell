package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostImage(
    @Json(name = "post_id") val postId: String,
    @Json(name = "url") var url: String,
    @Json(name = "order") var order: Int
)
