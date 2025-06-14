package com.example.myapplication.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Conversation
import model.Message
import store.DataStore
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val myRepository: MyRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatViewState())
    val state  = _state.asStateFlow()
    private lateinit var _conversation : Conversation

    fun sendMessage(content: String){
        val message = Message(
            id ="",
            conversationId = _conversation.id,
            senderId = DataStore.user?.id ?: "",
            content = content,
            createdAt = LocalDateTime.now(),
            avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
        )
        _state.update { it.copy(messages = it.messages + message) }
    }

     fun getMessages(conversationId : String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
//        val result = myRepository.getConversations()
//        result.fold(
//            ifLeft = { error ->
//                _state.update { it.copy(error = error.error.message) }
//                sendEvent(Event.Toast(error.error.message))
//            },
//            ifRight = { conversations ->
//                _state.update { it.copy(conversations = conversations) }
//            }
//        )
            val messages = listOf(
                Message(
                    id = "msg_1",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "Hello, I'm buyer 1",
                    createdAt = LocalDateTime.now().minusMinutes(10),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_2",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "Hi, I'm seller 1",
                    createdAt = LocalDateTime.now().minusMinutes(9),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_3",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "I have a question about the product.",
                    createdAt = LocalDateTime.now().minusMinutes(8),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_4",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "Sure, go ahead.",
                    createdAt = LocalDateTime.now().minusMinutes(7),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_5",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "Is this still available?",
                    createdAt = LocalDateTime.now().minusMinutes(6),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_6",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "Yes, it is.",
                    createdAt = LocalDateTime.now().minusMinutes(5),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_7",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "Can you ship it tomorrow?",
                    createdAt = LocalDateTime.now().minusMinutes(4),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_8",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "Absolutely. I can send it first thing in the morning.By the way, i gonna dead after send you that, remember to find my body",
                    createdAt = LocalDateTime.now().minusMinutes(3),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_9",
                    conversationId = conversationId,
                    senderId = "buyer_1",
                    content = "Perfect! Thank you.",
                    createdAt = LocalDateTime.now().minusMinutes(2),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
                ),
                Message(
                    id = "msg_10",
                    conversationId = conversationId,
                    senderId = "seller_1",
                    content = "You're welcome!",
                    createdAt = LocalDateTime.now().minusMinutes(1),
                    avatarURL = "https://images.unsplash.com/photo-1571757767119-68b8d"
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
            _state.update { it.copy(isLoading = false,messages = messages.sortedBy { it.createdAt }) }
        }
    }

}