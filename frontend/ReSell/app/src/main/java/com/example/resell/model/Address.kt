package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "id")
    val id: String,

    @Json(name = "user_id")
    val userId: String,

    @Json(name = "ward_id")
    var wardId: String,

    @Json(name = "detail")
    var detail: String,

    @Json(name = "is_default")
    var isDefault: Boolean,

    @Json(name = "ward")
    val ward: Ward? = null,
    @Json(name = "fullname")
    val fullname: String? = null,
    @Json(name = "phone")
    val phone: String? = null,

)
