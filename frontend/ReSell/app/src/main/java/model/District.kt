package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class District(
    val id: String,
    val name : String,
    val provinceId: String,
    val wards: List<Ward>
)
