package com.example.resell.ui.repository

import arrow.core.Either
import com.example.resell.ui.ApiService
import com.example.resell.ui.RefreshApiService
import com.example.resell.ui.domain.NetworkError
import com.example.resell.ui.mapper.toNetworkError
import model.AuthToken
import model.ChangePasswordRequest
import model.LoginRequest
import model.LoginResponse
import model.LoginType
import model.RefreshRequest
import model.RegisterRequest
import model.UpdateProfileRequest
import model.User
import store.TokenManager
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val refreshApiService: RefreshApiService,
    private val tokenManager: TokenManager
): UserRepository {
    override suspend fun registerUser(
        username: String,
        email: String,
        phone: String,
        password: String
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = RegisterRequest(
                username = username,
                email = email,
                phone = phone,
                password = password
            )
            apiService.register(request)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun loginUser(
        identifier: String,
        password: String,
        loginType: LoginType
    ): Either<NetworkError, LoginResponse> {
        return Either.catch {
            val request = LoginRequest(
                identifier = identifier,
                password = password,
                loginType = loginType
            )

            val response = apiService.login(request)
            tokenManager.saveToken(response.token)
            response
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updateInfo(request: UpdateProfileRequest): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updateProfile(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            apiService.changePassword(request)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteUser(userID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteUser(userID)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun followUser(userID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteUser(userID)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getAllFollows(): Either<NetworkError, List<User>> {
        return Either.catch {
            apiService.getAllFollow()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun unfollowUser(userID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.unfollowUser(userID)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun refreshSession(refreshToken: String): Either<NetworkError, AuthToken> {
        return Either.catch {
            val request = RefreshRequest(refreshToken)
            val response = refreshApiService.refreshSession(request).execute()

            if (response.isSuccessful) {
                val token = response.body() ?: throw Exception("Empty response body")
                tokenManager.saveToken(token)
                token
            } else {
                throw Exception("Refresh failed: ${response.code()} ${response.message()}")
            }
        }.mapLeft { it.toNetworkError() }
    }
}