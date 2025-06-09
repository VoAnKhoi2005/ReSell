package com.example.myapplication.ui

import arrow.core.Either
import com.example.myapplication.ui.domain.NetworkError
import com.example.myapplication.ui.mapper.toNetworkError
import com.example.myapplication.ui.model.Post
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