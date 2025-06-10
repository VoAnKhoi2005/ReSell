package helper.api.repository

import helper.api.ApiClient
import model.transaction.LoginRequest
import model.transaction.LoginResponse
import model.transaction.LoginType

class UserRepository {
    private val api = ApiClient.apiService

    suspend fun loginUser(identifier: String, password: String, loginType: LoginType): Result<LoginResponse>{
        return try {
            val request = LoginRequest(identifier, password, loginType)
            val response = api.login(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}