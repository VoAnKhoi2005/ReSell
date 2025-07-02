package com.example.resell.repository

import arrow.core.Either
import com.example.resell.model.CreateZaloPayPaymentResponse
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PaymentRepository {
    override suspend fun createZaloPayPayment(orderID: String): Either<NetworkError, CreateZaloPayPaymentResponse> {
        return Either.catch {
            apiService.createZaloPayPayment(orderID)
        }.mapLeft { it.toNetworkError() }
    }
}