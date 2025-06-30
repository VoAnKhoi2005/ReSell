package com.example.resell.ui.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Category
import com.example.resell.model.Post
import com.example.resell.repository.CategoryRepository
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
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _postList = MutableStateFlow<List<ProductPost>>(emptyList())
    val postList: StateFlow<List<ProductPost>> = _postList
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private var currentPage = 1
    private val pageSize = 20
    private var canLoadMore = false
    init {
        getPosts()
        viewModelScope.launch {
            val category = categoryRepository.getAllCategory()
            category.fold(
                {
                    Log.e("Home View","${it.message}")
                },
                {result->
                    _categoryList.value = result.filter { it.parentId == null }
                }
            )
        }

    }
    fun loadMore(){
        Log.d("Home","Loadmore")
        if (!canLoadMore) return
        viewModelScope.launch {
            _isLoading.value = true

            val result = postRepository.getPosts(
                page = currentPage,
                limit = pageSize,
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
                    canLoadMore = false
                },
                { response ->
                    val posts = response.data ?: emptyList()
                    canLoadMore = response.hasMore
                    currentPage++
                    Log.d("HomeViewModel", "Tổng số post: ${posts.size}")

                    _postList.value =_postList.value + posts.map { post ->
                        val createdAt = post.createdAt
                        val timeText = getRelativeTime(createdAt)

                        ProductPost(
                            id = post.id,
                            title = post.title,
                            time = timeText,
                            imageUrl = post.thumbnail,
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

    private fun getPosts() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = postRepository.getPosts(
                page = currentPage,
                limit = pageSize,
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
                    canLoadMore = false
                },
                { response ->
                    val posts = response.data ?: emptyList()
                    canLoadMore = response.hasMore
                    currentPage++
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

