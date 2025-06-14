package com.example.resell.ui


import model.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //region User
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Boolean

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @PUT("api/user/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Boolean

    @PUT("api/user/change_password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Boolean

    @DELETE("api/user/delete/{user_id}")
    suspend fun deleteUser(@Path("user_id") userID: String): Boolean

    @POST("api/user/follow/{user_id}")
    suspend fun followUser(@Path("user_id") userID: String): Boolean

    @GET("api/user/follow/all")
    suspend fun getAllFollow(): List<User>

    @DELETE("api/user/unfollow/{user_id}")
    suspend fun unfollowUser(@Path("user_id") userID: String): Boolean
    //endregion



    @GET("")
    suspend fun getPosts():List<Post>
}