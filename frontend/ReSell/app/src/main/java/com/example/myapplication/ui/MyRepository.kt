package com.example.myapplication.ui

import model.*
import arrow.core.Either
import com.example.myapplication.ui.domain.NetworkError

interface MyRepository {
    suspend fun getPosts(): Either<NetworkError, List<Post>>
}