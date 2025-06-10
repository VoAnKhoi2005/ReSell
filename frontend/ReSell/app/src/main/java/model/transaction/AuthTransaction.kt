package model.transaction
import model.User
import model.Token

enum class LoginType(private val value: String) {
    USERNAME("username"),
    EMAIL("email"),
    PHONE("phone");

    override fun toString(): String = value
}

data class LoginRequest(
    val identifier: String,
    val password: String,
    val loginType: LoginType
)

data class LoginResponse(
    val user: User,
    val token: Token
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val phone: String,
    val password: String
)