package com.example.resell.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostImage(
    val postId: String,
    var url: String,
    var order: Int
)
