package com.example.resell.network

import model.AuthToken
import model.RefreshRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApiService {
    @POST("api/auth/refresh")
    fun refreshSession(@Body request: RefreshRequest): Call<AuthToken>
}