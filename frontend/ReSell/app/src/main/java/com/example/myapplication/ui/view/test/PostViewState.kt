package com.example.myapplication.ui.view.test

import com.example.myapplication.ui.model.Post

data class PostViewState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String?= null
)
