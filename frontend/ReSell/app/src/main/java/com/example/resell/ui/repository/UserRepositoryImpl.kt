package com.example.resell.ui.repository

import arrow.core.Either
import com.example.resell.ui.ApiService
import com.example.resell.ui.domain.NetworkError
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): UserRepository {
    override suspend fun registerUser(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateInfo(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun changePassword(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun followUser(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFollows(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun unfollowUser(): Either<NetworkError, Boolean> {
        TODO("Not yet implemented")
    }
}