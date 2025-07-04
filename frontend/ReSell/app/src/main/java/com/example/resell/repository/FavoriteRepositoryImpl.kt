package com.example.resell.repository

import FavoritePost
import arrow.core.Either
import com.example.resell.model.LikePostRequest
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): FavoriteRepository {
    override suspend fun getFavoritePosts(): Either<NetworkError, List<FavoritePost>> {
        return Either.catch {
            apiService.getFavoritePosts()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun likePost(postID: String): Either<NetworkError, FavoritePost> {
        return Either.catch {
            apiService.likePost(LikePostRequest(postID))
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun unlikePost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            val response = apiService.unlikePost(postID)
            if (response.isSuccessful) {
                true
            } else {
                val errorBody = response.errorBody()?.string()
                throw Exception("API error ${response.code()}: $errorBody")
            }
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun isFavorite(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.isFavorite(postID)
        }.mapLeft { it.toNetworkError() }
    }

}