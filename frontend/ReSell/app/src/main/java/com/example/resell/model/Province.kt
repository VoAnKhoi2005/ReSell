package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Province(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "districts") val districts: List<District>
)

