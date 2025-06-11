package model

data class Province(
    val id: String,
    val name: String,
    val districts: List<District>
)
