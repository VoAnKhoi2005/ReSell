package com.example.resell.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.resell.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import com.example.resell.repository.PostRepository
import com.example.resell.repository.UserRepository
import java.time.LocalDateTime
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

            val result = messageRepository.getAllConversations()

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
                    val conversationCards = conversations.mapNotNull { conversation ->
                        val postResult = postRepository.getPostByID(conversation.postId)

                        if (postResult.isRight()) {
                            val post = (postResult as Either.Right).value
                            ConversationCard(conversation, post)
                        } else {
                            null // Bỏ qua nếu 1 trong 2 lỗi
                        }
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            conversationCards = conversationCards,
                            error = null
                        )
                    }
                }
            )
        }
    }
}

