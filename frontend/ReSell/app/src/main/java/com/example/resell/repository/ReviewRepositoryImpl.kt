package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ReviewRepository {
    override suspend fun createReview(
        orderID: String,
        rating: Int,
        comment: String
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = CreateReviewRequest(
                orderID = orderID,
                rating = rating,
                comment = comment
            )

            apiService.createReview(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getReviewByBuyerID(buyerID: String): Either<NetworkError, List<Review>> {
        return Either.catch {
            apiService.getReviewByBuyerID(buyerID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getReviewByPostID(postID: String): Either<NetworkError, Review> {
        return Either.catch {
            apiService.getReviewByPostID(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getReviewByOrderID(orderID: String): Either<NetworkError, Review> {
        return Either.catch {
            apiService.getReviewByOrderID(orderID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteReviewByOrderID(orderID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteReviewByOrderID(orderID)
        }.mapLeft { it.toNetworkError() }
    }
}