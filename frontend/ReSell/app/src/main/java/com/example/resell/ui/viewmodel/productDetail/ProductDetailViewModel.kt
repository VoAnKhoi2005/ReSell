package com.example.resell.ui.viewmodel.productDetail

import android.util.Log
import android.widget.Toast
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
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    var errorMessage by mutableStateOf<String?>(null)
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
                    errorMessage = "Không thể tải bài đăng"
                    _isLoading.value = false
                },
                ifRight = { post ->
                    postDetail = post
                    _isLoading.value = false
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
                    errorMessage = "Lỗi lấy cuộc trò chuyện"

                    _isLoading.value = false
                },
                ifRight = { conversation ->
                    Log.d("Conversation: ","${conversation}")
                   if (conversation.isExist) {
                       NavigationController.navController.navigate("chat/${conversation.conversation!!.id}")
                   }
                   else {
                        Log.d("ProductDetail","Chưa có cuộc trò chuyện")
                       val createResult = messageRepository.createConversation(ReactiveStore<User>().item.value!!.id,postDetail!!.userID,postId)

                       createResult.fold(
                           ifLeft = { createError ->
                               Log.e("ProductDetail", "Lỗi tạo cuộc trò chuyện: ${createError.message}")
                               errorMessage = "Lỗi tạo cuộc trò chuyện"
                           },
                           ifRight = { newConversation ->
                               Log.d("ProductDetail", "Tạo cuộc trò chuyện thành công: ${newConversation}")
                               NavigationController.navController.navigate("chat/${newConversation.id}")
                           }
                       )
                   }
                    _isLoading.value = false
            }
            )
        }
    }
}
