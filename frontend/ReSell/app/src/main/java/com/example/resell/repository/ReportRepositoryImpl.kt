package com.example.resell.repository

import arrow.core.Either
import com.example.resell.model.ReportPostRequest
import com.example.resell.model.ReportUserRequest
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ReportRepository {
    override suspend fun createReportUser(
        reportedUserID: String,
        description: String
    ): Either<NetworkError, Unit> {
        return Either.catch {
            val request = ReportUserRequest(
                reportedUserID,
                description
            )

            val response = apiService.createReportUser(request)

            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun createReportPost(
        reportedPostID: String,
        description: String
    ): Either<NetworkError, Unit> {
        return Either.catch {
            val request = ReportPostRequest(
                reportedPostID,
                description
            )

            val response = apiService.createReportPost(request)

            if (response.isSuccessful) {
                Unit
            } else {
                throw HttpException(response)
            }
        }.mapLeft { it.toNetworkError() }
    }
}