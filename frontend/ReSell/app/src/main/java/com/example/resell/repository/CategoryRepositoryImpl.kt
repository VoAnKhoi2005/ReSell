package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.Category
import com.example.resell.model.CreateCategoryRequest
import com.example.resell.model.UpdateCategoryRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): CategoryRepository {
    override suspend fun getAllCategory(): Either<NetworkError, List<Category>> {
        return Either.catch {
            apiService.getAllCategory()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getCategoryByID(categoryID: String): Either<NetworkError, Category> {
        return Either.catch {
            apiService.getCategoryByID(categoryID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun createCategory(
        name: String,
        parentCategoryID: String
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = CreateCategoryRequest(
                name = name,
                parentCategoryID = parentCategoryID
            )

            apiService.createCategory(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updateCategory(categoryID: String, request: UpdateCategoryRequest): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updateCategory(categoryID, request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteCategory(categoryID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteCategory(categoryID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getChildrenCategories(categoryID: String): Either<NetworkError, List<Category>> {
        return Either.catch {
            apiService.getCategoryChildren(categoryID)
        }.mapLeft { it.toNetworkError() }
    }
}
