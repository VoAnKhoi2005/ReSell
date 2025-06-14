package com.example.resell.ui.repository

import com.example.resell.ui.ApiService
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): CategoryRepository{

}
