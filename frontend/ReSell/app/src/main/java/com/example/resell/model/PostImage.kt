package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostImage(
    @Json(name = "image_url") var url: String,
    @Json(name = "image_order") var order: Int
)
