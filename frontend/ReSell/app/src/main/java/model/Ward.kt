package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ward (
    val id: String,
    val name: String,
    val districtId: String
)