package com.example.resell.ui.repository

import model.*
import arrow.core.Either
import arrow.core.EitherNel
import com.example.resell.ui.domain.NetworkError

interface UserRepository{
    suspend fun registerUser(): Either<NetworkError, Boolean>
    suspend fun loginUser(): Either<NetworkError, Boolean>
    suspend fun updateInfo(): Either<NetworkError, Boolean>
    suspend fun changePassword(): Either<NetworkError, Boolean>
    suspend fun deleteUser(): Either<NetworkError, Boolean>
    suspend fun followUser(): Either<NetworkError, Boolean>
    suspend fun getAllFollows(): Either<NetworkError, Boolean>
    suspend fun unfollowUser(): Either<NetworkError, Boolean>
}

interface AddressRepository{

}

interface CategoryRepository{

}

interface PostRepository {
    suspend fun getPosts(): Either<NetworkError, List<Post>>
}

interface OrderRepository{

}

interface ReviewRepository{

}

interface MessageRepository{

}