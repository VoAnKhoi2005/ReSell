package com.example.resell.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Province(
    val id: String,
    val name: String,
    val districts: List<com.example.resell.model.District>
)
