package com.example.resell.repository

import arrow.core.Either
import com.example.resell.model.GetNotificationByBatchResponse
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.network.ApiService
import com.example.resell.model.Notification
import com.example.resell.model.NotificationType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): NotificationRepository {
    override suspend fun getNotificationsByBatch(
        batchSize: Int,
        page: Int
    ): Either<NetworkError, GetNotificationByBatchResponse> {
        return Either.catch {
            apiService.getNotificationsByBatch(batchSize, page)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getNotificationsByDate(date: LocalDate): Either<NetworkError, List<Notification>> {
        return Either.catch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = date.format(formatter)

            apiService.getNotificationsByDate(formattedDate)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getNotificationsByType(type: NotificationType): Either<NetworkError, List<Notification>> {
        return Either.catch {
            apiService.getNotificationsByType(type)
        }.mapLeft { it.toNetworkError() }
    }
}