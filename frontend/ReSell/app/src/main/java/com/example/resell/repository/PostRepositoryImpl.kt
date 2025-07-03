package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.*
import com.example.resell.network.ApiError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PostRepository {
    override suspend fun getPosts(
        page: Int,
        limit: Int,
        status: String?,
        minPrice: Int?,
        maxPrice: Int?,
        provinceID: String?,
        districtID: String?,
        wardID: String?,
        userID: String?,
        categoryID: String?,
        isFavorite: Boolean?,
        isFollowing: Boolean?,
        search: String?
    ): Either<NetworkError, GetPostsResponse> {
        return Either.catch {
            apiService.getPosts(
                page, limit,
                status,
                minPrice, maxPrice,
                provinceID, districtID, wardID,
                userID,
                categoryID,
                isFavorite,
                isFollowing,
                search
            )
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOwnPosts(
        page: Int,
        limit: Int,
        status: String?,
        minPrice: Int?,
        maxPrice: Int?,
        provinceID: String?,
        districtID: String?,
        wardID: String?,
        categoryID: String?,
        isFavorite: Boolean?,
        isFollowing: Boolean?,
        search: String?
    ): Either<NetworkError, GetPostsResponse> {
        return Either.catch {
            apiService.getOwnPosts(
                page, limit,
                status,
                minPrice, maxPrice,
                provinceID, districtID, wardID,
                categoryID,
                isFavorite,
                isFollowing,
                search
            )
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getRecommendationPosts(
        page: Int,
        limit: Int
    ): Either<NetworkError, GetPostsResponse> {
        return Either.catch {
            apiService.getRecommendationPosts(page, limit)
        }.mapLeft { it.toNetworkError() }
    }

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
        price: Int
    ): Either<NetworkError, Post> {
        return Either.catch {
            val request = CreatePostRequest(
                title = title,
                description = description,
                categoryID = categoryID,
                wardID = addressID,
                price = price
            )

            apiService.createPost(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updatePost(postID: String, request: UpdatePostRequest): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updatePost(postID, request)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun hardDeletePost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.hardDeletePost(postID)
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun softDeletePost(postID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.softDeletePost(postID)
            true
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
            true
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun uploadPostImage(postID: String, images: List<File>): Either<NetworkError, ImageUploadResponse> {
        return Either.catch {
            if (images.isEmpty())
                return Either.Left(NetworkError(
                    code = 400,
                    error = ApiError.UnknownResponse,
                    message = "Image list is empty"
                ))

            val parts = images.map { image ->
                val requestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", image.name, requestBody)
            }

            apiService.uploadPostImages(postID, parts)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deletePostImages(
        postID: String,
        imageURLs: List<String>
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deletePostImages(postID, DeletePostImagesRequest(imageURLs))
            true
        }.mapLeft { it.toNetworkError() }
    }
}