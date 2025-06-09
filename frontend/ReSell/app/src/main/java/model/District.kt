package model

data class District(
    val id: String,
    val name : String,
    val provinceId: String,
    val wards: List<Ward>
)
