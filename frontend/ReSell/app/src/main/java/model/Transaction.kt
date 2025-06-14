package model

import com.squareup.moshi.JsonClass


//region User
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
//endregion

//region Address
@JsonClass(generateAdapter = true)
data class CreateAddressRequest(
    val wardID: String,
    val detail: String,
    val isDefault: Boolean
)

@JsonClass(generateAdapter = true)
data class UpdateAddressRequest(
    val wardID: String? = null,
    val detail: String? = null,
    val isDefault: Boolean? = null
)
//endregion

//region Category
@JsonClass(generateAdapter = true)
data class CreateCategoryRequest(
    val name: String,
    val parentCategoryID: String
)

@JsonClass(generateAdapter = true)
data class UpdateCategoryRequest(
    val name: String? = null,
    //val parentCategoryID: String? = null,
)
//endregion

//region Post
//endregion

//region Order
@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    val postID: String,
    val addressID: String,
    val total: Double
)
//endregion