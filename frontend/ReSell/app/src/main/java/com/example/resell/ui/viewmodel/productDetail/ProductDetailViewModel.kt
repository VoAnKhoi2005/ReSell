package com.example.resell.ui.viewmodel.productDetail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Post
import com.example.resell.model.PostData
import com.example.resell.model.UserStat
import com.example.resell.repository.FavoriteRepository
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.repository.UserRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.navigation.NavigationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val messageRepository: MessageRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val postId: String = savedStateHandle["id"] ?: ""

    var postDetail by mutableStateOf<Post?>(null)
        private set
    var statSeller by mutableStateOf<UserStat?>(null)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite


    var sellerPosts by mutableStateOf<List<PostData>>(emptyList())
        private set

    var relatedPosts by mutableStateOf<List<PostData>>(emptyList())
        private set

    init {
        getPostDetail()
    }

    private fun getPostDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postRepository.getPostByID(postId)
            result.fold(
                ifLeft = {error ->
                    Log.e("HomeViewModel", "Lỗi lấy bài đăng: ${error.message}")
                    _isLoading.value = false
                },
                ifRight = { post ->
                    postDetail = post

                    val seller = postRepository.getPosts(
                        page =1,
                        limit = 10,
                        status = "approved",
                        userID = postDetail?.userID,
                    )
                    val related = postRepository.getPosts(
                        page = 1,
                        limit =10,
                        status = "approved",
                        categoryID = postDetail?.categoryID
                    )
                    val stat = userRepository.getUserStat(postDetail?.userID?:"")
                    if (seller.isRight() && related.isRight()&&stat.isRight()) {
                        val _sellerPosts = seller.orNull()?.data ?: emptyList()
                        val _relatedPosts = related.orNull()?.data ?: emptyList()
                        statSeller = stat.orNull()
                        sellerPosts = _sellerPosts
                        relatedPosts = _relatedPosts
                    }
                    _isLoading.value = false
                }
            )
            favoriteRepository.getFavoritePosts().fold(
                {

                },
                {
                    it->
                    _isFavorite.value= it.any { it.postId == postId }
                }
            )


        }
    }
    fun toggleFavorite() {
        viewModelScope.launch {
            val result = if (_isFavorite.value) {
                favoriteRepository.unlikePost(postId)
            } else {
                favoriteRepository.likePost(postId)
            }
            result.fold(
                {
                    Log.e("Like post", "${it.message}")
                },
                {
                    _isFavorite.value = !_isFavorite.value
                }
            )
        }
    }
    fun openConversation(){
        viewModelScope.launch {
            _isLoading.value = true
            val result = messageRepository.getConversationByPostAndUserID(postId)
            result.fold (
                ifLeft = {error ->
                    Log.e("ProductDetail", "Lỗi lấy cuộc trò chuyện: ${error.message}")

                    _isLoading.value = false
                },
                ifRight = { conversation ->
                    Log.d("Conversation: ","${conversation}")
                   if (conversation.isExist) {
                       NavigationController.navController.navigate("chat/${conversation.conversation!!.id}")
                   }
                   else {
                        Log.d("ProductDetail","Chưa có cuộc trò chuyện")

                       ReactiveStore<Post>().set(postDetail)
                       NavigationController.navController.navigate("chat/")
                   }
                    _isLoading.value = false
            }
            )
        }
    }
}
