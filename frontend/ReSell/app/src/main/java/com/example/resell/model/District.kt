package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class District(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "province_id") val provinceId: String,

    @Json(name = "wards") val wards: List<Ward>? = null,
    @Json(name = "province") val province: Province? = null
)
