package com.example.resell.ui

import arrow.core.Either
import com.example.resell.ui.domain.NetworkError
import com.example.resell.ui.mapper.toNetworkError
import model.*
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): MyRepository {
    override suspend fun getPosts(): Either<NetworkError, List<Post>> {
        return Either.catch {
            apiService.getPosts()
        }.mapLeft { it.toNetworkError()  }
    }
}