package com.example.resell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Post
import com.example.resell.model.PostData
import com.example.resell.model.PostStatus
import com.example.resell.model.User
import com.example.resell.repository.PostRepository
import com.example.resell.store.ReactiveStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostManagementViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostData>>(emptyList())
    val posts: StateFlow<List<PostData>> = _posts

    val pendingPosts = _posts.map { it.filter { post -> post.status == "pending" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val approvedPosts = _posts.map { it.filter { post -> post.status == "approved" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val rejectedPosts = _posts.map { it.filter { post -> post.status == "rejected" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val soldPosts = _posts.map { it.filter { post -> post.status == "sold" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val hidePosts = _posts.map { it.filter { post -> post.status ==  "deleted" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun getPosts() {
        viewModelScope.launch {
            var currentPage = 1
            var isHasMore = true

            while (isHasMore) {
                val result = postRepository.getOwnPosts(
                    page = currentPage,
                    limit = 100
                )

                result.fold(
                    { error ->
                        isHasMore = false // hoặc log lỗi
                    },
                    { response ->
                        isHasMore = response.hasMore
                        _posts.value =
                            _posts.value + (response.data?.filterIsInstance<PostData>()
                                ?: emptyList())
                        currentPage++
                    }
                )
            }
        }
    }
    }
