package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.*
import com.example.resell.store.AuthTokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
): UserRepository {
    override suspend fun firebaseAuth(
        firebaseIDToken: String,
        username: String?,
        fullname: String?,
        password: String?
    ): Either<NetworkError, FirebaseAuthResponse> {
        return Either.catch {
            val request = FirebaseAuthRequest(
                firebaseIDToken = firebaseIDToken,
                username = username,
                fullname = fullname,
                password = password,
            )

            val response = apiService.firebaseAuth(request)
            if (!response.firstTimeLogin) {
                response.token?.let { tokenManager.saveToken(it) }
            }
            response
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun verifyToken(): Either<NetworkError, VerifyTokenResponse> {
        return Either.catch {
            val response = apiService.verifyToken()
            tokenManager.saveToken(response.token)
            response
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

    override suspend fun updateInfo(request: UpdateProfileRequest): Either<NetworkError, User> {
        return Either.catch {
            apiService.updateProfile(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getUserStat(userID: String): Either<NetworkError, UserStat> {
        return Either.catch {
            apiService.getUserStat(userID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun searchUsername(query: String): Either<NetworkError, List<User>> {
        return Either.catch {
            apiService.searchUsername(query)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            apiService.changePassword(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteUser(userID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteUser(userID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun followUser(userID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.followUser(userID)
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
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun uploadAvatar(avatar: File): Either<NetworkError, AvatarUploadResponse> {
        return Either.catch {
            val requestBody = avatar.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", avatar.name, requestBody)

            apiService.uploadAvatar(part)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun uploadCover(cover: File): Either<NetworkError, CoverUploadResponse> {
        return Either.catch {
            val requestBody = cover.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("cover", cover.name, requestBody)

            apiService.uploadCover(part)
        }.mapLeft { it.toNetworkError() }
    }
}