package model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val username: String,
    val email: String,
    val phone: String,
    val password: String
)

enum class LoginType{
    email, phone, username
}

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String,
    val loginType: LoginType
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val user: User,
    val token: AuthToken
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val fullName: String? = null,
    val citizenId: String? = null
)

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)