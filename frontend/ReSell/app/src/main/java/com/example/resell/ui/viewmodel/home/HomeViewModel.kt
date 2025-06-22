package com.example.resell.ui.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Post
import com.example.resell.repository.PostRepository
import com.example.resell.ui.screen.home.ProductPost
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
                status = null,
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
                        val imageUrl = post.images.sortedBy { it.order }.firstOrNull()?.url ?: ""
                        val timeText = getRelativeTime(createdAt)

                        Log.d(
                            "HomeViewModel",
                            "Post: ${post.title}, createdAt=$createdAt, time=$timeText, imageUrl=$imageUrl"
                        )

                        ProductPost(
                            title = post.title,
                            time = timeText,
                            imageUrl = imageUrl,
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

fun getRelativeTime(time: LocalDateTime?): String {
    if (time == null) return "Không rõ"

    val now = LocalDateTime.now()
    val duration = Duration.between(time, now)

    return when {
        duration.toMinutes() < 1 -> "Vừa xong"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
        duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
        duration.toDays() == 1L -> "Hôm qua"
        duration.toDays() < 7 -> "${duration.toDays()} ngày trước"
        duration.toDays() < 30 -> "${duration.toDays() / 7} tuần trước"
        duration.toDays() < 365 -> "${duration.toDays() / 30} tháng trước"
        else -> "${duration.toDays() / 365} năm trước"
    }
}