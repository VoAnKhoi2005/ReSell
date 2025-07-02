package com.example.resell.repository

import arrow.core.Either
import com.example.resell.model.SubscriptionPlan
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): SubscriptionRepository {
    override suspend fun getAllPlans(): Either<NetworkError, List<SubscriptionPlan>> {
        return Either.catch {
            apiService.getAllPlans()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getPlanByID(planID: String): Either<NetworkError, SubscriptionPlan> {
        return Either.catch {
            apiService.getPlanByID(planID)
        }.mapLeft { it.toNetworkError() }
    }
}