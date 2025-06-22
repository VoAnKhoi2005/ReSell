package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PostRepository {
    override suspend fun getPostByID(postID: String): Either<NetworkError, Post> {
        return Either.catch {
            apiService.getPostByID(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun createPost(
        title: String,
        description: String,
        categoryID: String,
        addressID: String,
        price: Double
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = CreatePostRequest(
                title = title,
                description = description,
                categoryID = categoryID,
                addressID = addressID,
                price = price
            )

            apiService.createPost(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updatePost(postID: String, request: UpdatePostRequest): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updatePost(postID, request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun hardDeletePost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.hardDeletePost(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun softDeletePost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.softDeletePost(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getDeletedPosts(): Either<NetworkError, List<Post>> {
        return Either.catch {
            apiService.getAllDeletedPosts()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun restoreDeletedPost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.restoreDeletedPost(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun uploadPostImage(postID: String): Either<NetworkError, ImageUploadResponse> {
        TODO("Not yet implemented")
    }
}