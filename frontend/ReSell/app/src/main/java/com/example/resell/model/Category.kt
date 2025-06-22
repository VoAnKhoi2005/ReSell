package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: String,
    @Json(name = "parent_id") val parentId: String,
    @Json(name = "name") val name: String
)
