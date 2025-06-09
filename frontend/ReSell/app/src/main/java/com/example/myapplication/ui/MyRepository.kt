package com.example.myapplication.ui

import com.example.myapplication.ui.model.*
import arrow.core.Either
import com.example.myapplication.ui.domain.NetworkError

interface MyRepository {
    suspend fun getPosts(): Either<NetworkError, List<Post>>
}