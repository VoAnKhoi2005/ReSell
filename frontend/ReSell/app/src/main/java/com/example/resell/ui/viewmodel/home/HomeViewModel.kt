package com.example.resell.ui.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Post
import com.example.resell.repository.PostRepository
import com.example.resell.ui.screen.home.ProductPost
import com.example.resell.util.getRelativeTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _postList = MutableStateFlow<List<ProductPost>>(emptyList())
    val postList: StateFlow<List<ProductPost>> = _postList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        getPosts()
    }

    private fun getPosts() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = postRepository.getPosts(
                page = 1,
                limit = 20,
                status = "approved",
                minPrice = null,
                maxPrice = null,
                provinceID = null,
                districtID = null,
                wardID = null,
                userID = null,
                categoryID = null
            )

            result.fold(
                { error ->
                    Log.e("HomeViewModel", "Lỗi lấy bài đăng: ${error.message}")
                    _postList.value = emptyList()
                },
                { response ->
                    val posts = response.data ?: emptyList()
                    Log.d("HomeViewModel", "Tổng số post: ${posts.size}")

                    _postList.value = posts.map { post ->
                        val createdAt = post.createdAt
                        val timeText = getRelativeTime(createdAt)

                        Log.d(
                            "HomeViewModel",
                            "Post: ${post.title}, createdAt=$createdAt, time=$createdAt, imageUrl=${post.thumbnail}"
                        )

                        ProductPost(
                            id = post.id,
                            title = post.title,
                            time = timeText,
                            imageUrl = post.thumbnail ?: "",
                            price = post.price,
                            category = post.category,
                            address = post.address
                        )
                    }
                }
            )

            _isLoading.value = false
        }
    }
}

