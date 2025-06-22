package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ward(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "district_id") val districtId: String
)