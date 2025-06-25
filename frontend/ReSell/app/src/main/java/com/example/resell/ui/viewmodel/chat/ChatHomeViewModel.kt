package com.example.resell.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.resell.repository.PostRepository
import com.example.resell.repository.UserRepository
import javax.inject.Inject


@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository

) : ViewModel() {
    private val _state = MutableStateFlow(ChatHomeViewState())
    val state  = _state.asStateFlow()
    init{
        getConversations()
    }

    private fun getConversations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = messageRepository.getAllUserConversationsDTO()

            result.fold(
                { error ->
                    _state.update {
                        Log.e("ChatHome", "Lỗi lấy cuộc trò chuyện: ${error.message}")
                        it.copy(
                            isLoading = false,
                            conversationCards = emptyList(),
                            error = error.message ?: "Lỗi không xác định"

                        )
                    }
                },
                { conversations ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            conversationCards = conversations,
                            error = null
                        )
                    }
                }
            )
        }
    }
}

