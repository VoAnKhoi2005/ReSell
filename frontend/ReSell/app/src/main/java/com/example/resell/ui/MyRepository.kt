package com.example.resell.ui

import model.*
import arrow.core.Either
import com.example.resell.ui.domain.NetworkError

interface MyRepository {
    suspend fun getPosts(): Either<NetworkError, List<Post>>
}