package com.example.resell.network

import com.example.resell.model.RefreshRequest
import com.example.resell.model.AuthToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApiService {
    @POST("api/auth/refresh")
    fun refreshSession(@Body request: RefreshRequest): Call<AuthToken>
}