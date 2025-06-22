package com.example.resell.network


data class NetworkError(
    val code: Int? = null,
    val error: ApiError,
    val message: String? = null,
    val errors: Map<String, String>? = null,
    val t: Throwable? = null
)

enum class ApiError(val defaultMessage: String) {
    NetworkError("Network error"),
    UnknownResponse("Unknown response"),
    UnknownError("Unknown error"),
}
