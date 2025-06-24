package com.example.resell.ui.viewmodel.productDetail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resell.model.Post
import com.example.resell.model.User
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.store.ReactiveStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val postId: String = savedStateHandle["id"] ?: ""

    var postDetail by mutableStateOf<Post?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        getPostDetail()
    }

    private fun getPostDetail() {
        viewModelScope.launch {
            isLoading = true
            val result = postRepository.getPostByID(postId)
            result.fold(
                ifLeft = {error ->
                    Log.e("HomeViewModel", "Lỗi lấy bài đăng: ${error.message}")
                    errorMessage = "Không thể tải bài đăng"
                    isLoading = false
                },
                ifRight = { post ->
                    postDetail = post
                    isLoading = false
                }
            )
        }
    }
    fun openConversation(){
        Log.d("TEST","CON")
        viewModelScope.launch {
            val result = messageRepository.getConversationByPostAndUserID(postId)
            result.fold (
                ifLeft = {error ->
                    Log.e("ProductDetail", "Lỗi lấy cuộc trò chuyện: ${error.message}")
                    errorMessage = "Lỗi lấy cuộc trò chuyện"
                    isLoading = false
                },
                ifRight = { conversation ->
                    Log.d("Conversation: ","${conversation}")
                   if (conversation.isExist) {

                   }
                   else {
                        Log.d("ProductDetail","Chưa có cuộc trò chuyện")
                   }
            }
            )
        }
    }
}
