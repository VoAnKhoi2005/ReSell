package com.example.resell.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import com.example.resell.model.User
import com.example.resell.repository.MessageRepository
import com.example.resell.store.ReactiveStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository
) : ViewModel() {
    val user = ReactiveStore<User>().item.value

    private val _state = MutableStateFlow(ChatViewState())
    val state = _state.asStateFlow()

    val conversationId: String = savedStateHandle["id"] ?: ""

    fun sendMessage(content: String) {
        val message = Message(
            id = "",
            conversationId = conversationId,
            senderId = user?.id ?: "",
            content = content,
            createdAt = LocalDateTime.now()
        )
        _state.update { it.copy(messages = it.messages + message) }
    }

    fun showLoading() {
        _state.update { it.copy(isLoading = true) }
    }

    fun hideLoading() {
        _state.update { it.copy(isLoading = false) }
    }

    fun getMessages(conversationId: String) {
        viewModelScope.launch {
            showLoading()

            val result = messageRepository.getLatestMessage(conversationId,10)

            result.fold(
                { error ->
                    _state.update {
                        Log.e("ChatHome", "Lỗi lấy cuộc trò chuyện: ${error.message}")
                        it.copy(
                            isLoading = false,
                            messages = emptyList(),
                            error = error.message ?: "Lỗi không xác định"

                        )
                    }
                },
                { messages ->


                    _state.update {
                        it.copy(
                            isLoading = false,
                            messages = messages,
                            error = null
                        )
                    }
                }
            )


        }
    }
}
