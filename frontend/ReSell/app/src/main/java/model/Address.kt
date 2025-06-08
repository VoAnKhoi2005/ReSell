package model

data class Address (
    val id: String,
    val userId: String,
    var wardId: String,
    var detail: String,
    var idDefault: Boolean
)