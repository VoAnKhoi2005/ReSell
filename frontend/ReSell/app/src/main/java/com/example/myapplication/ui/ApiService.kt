package com.example.myapplication.ui

import com.example.myapplication.ui.model.*
import retrofit2.http.GET

interface ApiService {
    @GET("")
    suspend fun getPosts():List<Post>
}