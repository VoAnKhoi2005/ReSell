package com.example.resell.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Address (
    val id: String,
    val userId: String,
    var wardId: String,
    var detail: String,
    var idDefault: Boolean,
    val ward: com.example.resell.model.Ward?
)