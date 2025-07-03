package com.example.resell.ui.viewmodel.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.PostData
import com.example.resell.repository.FavoriteRepository
import com.example.resell.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _favoritePosts = MutableStateFlow<List<PostData>>(emptyList())
    val favoritePosts: StateFlow<List<PostData>> = _favoritePosts

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchFavoritePosts() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = postRepository.getPosts(
                page = 1,
                limit = 100,
                isFavorite = true
            )

            result.fold(
                ifLeft = {
                    Log.e("FavoriteViewModel", "Lỗi khi fetch favorite posts: ${it.message}")
                    _favoritePosts.value = emptyList()
                },
                ifRight = { response ->
                    val posts = response.data ?: emptyList()
                    Log.d("FavoriteViewModel", "Fetched ${posts.size} favorite posts")
                    _favoritePosts.value = posts
                }
            )

            _isLoading.value = false
        }
    }

}
