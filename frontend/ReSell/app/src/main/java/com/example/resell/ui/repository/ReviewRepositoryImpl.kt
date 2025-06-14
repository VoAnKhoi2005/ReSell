package com.example.resell.ui.repository

import com.example.resell.ui.ApiService
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ReviewRepository {}