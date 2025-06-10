package helper.api

import model.transaction.LoginRequest
import model.transaction.LoginResponse
import model.transaction.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest)

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}