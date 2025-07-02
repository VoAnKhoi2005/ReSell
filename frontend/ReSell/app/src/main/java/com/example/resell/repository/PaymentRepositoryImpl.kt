package com.example.resell.repository

import arrow.core.Either
import com.example.resell.model.CreateTransactionRequest
import com.example.resell.model.CreateTransactionResponse
import com.example.resell.model.PaymentMethod
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PaymentRepository {
    override suspend fun getAllPaymentMethods(): Either<NetworkError, List<PaymentMethod>> {
        return Either.catch {
            apiService.getAllPaymentMethods()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getPaymentMethodByID(paymentMethodID: String): Either<NetworkError, PaymentMethod> {
        return Either.catch {
            apiService.getPaymentMethodByID(paymentMethodID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun createTransaction(orderID: String): Either<NetworkError, CreateTransactionResponse> {
        return Either.catch {
            apiService.createTransaction(CreateTransactionRequest(orderID))
        }.mapLeft { it.toNetworkError() }
    }
}