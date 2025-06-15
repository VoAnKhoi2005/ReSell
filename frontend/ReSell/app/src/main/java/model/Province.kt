package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Province(
    val id: String,
    val name: String,
    val districts: List<District>
)
