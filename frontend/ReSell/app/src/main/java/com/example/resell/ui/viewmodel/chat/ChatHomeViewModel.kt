package com.example.resell.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Conversation
import model.Message
import java.time.LocalDateTime
import javax.inject.Inject






@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val myRepository: MessageRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatHomeViewState())
    val state  = _state.asStateFlow()
    init{
        getConversations()
    }

    private fun getConversations(){
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
        val list = mutableListOf<Conversation>()
        for (i in 1..3) {
            val conversationId = "conv_$i"
            val buyerId = "buyer_$i"
            val sellerId = "seller_$i"

            val messages = listOf(
                Message(
                    id = "msg_${i}_1",
                    conversationId = conversationId,
                    senderId = buyerId,
                    content = "Hello, I'm buyer $i",
                    createdAt = LocalDateTime.now(),

                ),
                Message(
                    id = "msg_${i}_2",
                    conversationId = conversationId,
                    senderId = sellerId,
                    content = "Hi, I'm seller $i",
                    createdAt = LocalDateTime.now(),

                )
            )

            val conversation = Conversation(
                id = conversationId,
                buyerId = buyerId,
                sellerId = sellerId,
                postId = "post_$i",
                createdAt = LocalDateTime.now(),
                messages = messages
            )

            list.add(conversation)
        }
        _state.update { it.copy(isLoading = false,conversations = list) }
    }
    }
}