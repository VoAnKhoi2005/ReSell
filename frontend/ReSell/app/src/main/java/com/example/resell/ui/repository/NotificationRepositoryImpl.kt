package com.example.resell.ui.repository

import arrow.core.Either
import com.example.resell.ui.domain.NetworkError
import com.example.resell.ui.mapper.toNetworkError
import com.example.resell.ui.network.ApiService
import model.Notification
import model.NotificationType
import java.time.LocalDate
import java.time.LocalDateTime
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
    ): Either<NetworkError, List<Notification>> {
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