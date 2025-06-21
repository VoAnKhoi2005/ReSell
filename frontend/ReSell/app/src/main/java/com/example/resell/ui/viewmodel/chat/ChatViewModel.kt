package com.example.resell.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import com.example.resell.repository.MessageRepository
import com.example.resell.store.DataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val myRepository: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatViewState())
    val state = _state.asStateFlow()

    private lateinit var _conversation: Conversation

    fun sendMessage(content: String) {
        val message = Message(
            id = "",
            conversationId = _conversation.id,
            senderId = DataStore.user?.id ?: "",
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

            val messages = listOf(
                Message(
                    id = "msg_1",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "Hello, I'm buyer 1",
                    createdAt = LocalDateTime.now().minusMinutes(10)
                ),
                Message(
                    id = "msg_2",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "Hi, I'm seller 1",
                    createdAt = LocalDateTime.now().minusMinutes(9)
                ),
                // ...more messages...
                Message(
                    id = "msg_10",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "You're welcome!",
                    createdAt = LocalDateTime.now().minusMinutes(1)
                )
            )

            _conversation = Conversation(
                id = conversationId,
                buyerId = "buyer_1",
                sellerId = "seller_1",
                postId = "post_1",
                createdAt = LocalDateTime.now(),
                messages = messages
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    messages = messages.sortedBy { it.createdAt }
                )
            }
        }
    }
}
