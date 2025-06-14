package com.example.resell.ui


import model.*
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    //region User
    @POST("/api/auth/register")
    suspend fun register()
    //endregion



    @GET("")
    suspend fun getPosts():List<Post>
}