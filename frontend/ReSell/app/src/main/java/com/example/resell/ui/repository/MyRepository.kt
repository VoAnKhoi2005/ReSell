package com.example.resell.ui.repository

import model.*
import arrow.core.Either
import arrow.core.EitherNel
import com.example.resell.ui.domain.NetworkError

interface UserRepository{
    suspend fun registerUser(username: String, email: String, phone: String, password: String): Either<NetworkError, Boolean>
    suspend fun loginUser(identifier: String, password: String, loginType: LoginType): Either<NetworkError, LoginResponse>
    suspend fun updateInfo(request: UpdateProfileRequest): Either<NetworkError, Boolean>
    suspend fun changePassword(oldPassword: String, newPassword: String): Either<NetworkError, Boolean>
    suspend fun deleteUser(userID: String): Either<NetworkError, Boolean>
    suspend fun followUser(userID: String): Either<NetworkError, Boolean>
    suspend fun getAllFollows(): Either<NetworkError, List<User>>
    suspend fun unfollowUser(userID: String): Either<NetworkError, Boolean>
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