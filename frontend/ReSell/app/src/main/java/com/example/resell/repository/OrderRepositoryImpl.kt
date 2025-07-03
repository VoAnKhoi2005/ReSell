package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.CreateOrderRequest
import com.example.resell.model.OrderStatus
import com.example.resell.model.ShopOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): OrderRepository {
    override suspend fun createOrder(
        postID: String,
        addressID: String,
        total: Int
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = CreateOrderRequest(
                postID =  postID,
                addressID = addressID,
                total = total
            )

            apiService.createOrder(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteOrder(orderID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteOrder(orderID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updateOrderStatus(
        orderID: String,
        status: OrderStatus
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updateOrderStatus(orderID, status)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOrderByPostID(postID: String): Either<NetworkError, ShopOrder> {
        return Either.catch {
            apiService.getOrderByPostID(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOrderByBuyerID(buyerID: String): Either<NetworkError, List<ShopOrder>> {
        return Either.catch {
            apiService.getOrderByBuyerID(buyerID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOrderBySellerID(sellerID: String): Either<NetworkError, List<ShopOrder>> {
        return Either.catch {
            apiService.getOrderBySellerID(sellerID)
        }.mapLeft { it.toNetworkError() }
    }
}