package com.example.resell.ui


import model.*
import retrofit2.http.GET

interface ApiService {
    @GET("")
    suspend fun getPosts():List<Post>
}