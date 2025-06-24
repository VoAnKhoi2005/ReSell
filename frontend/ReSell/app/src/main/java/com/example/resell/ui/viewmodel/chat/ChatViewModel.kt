package com.example.resell.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import com.example.resell.model.Post
import com.example.resell.model.User
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.screen.chat.chathomescreen.ConversationCard
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val user = ReactiveStore<User>().item.value

    private val _state = MutableStateFlow(ChatViewState())
    val state = _state.asStateFlow()

    var conversationId: String = savedStateHandle["conversationId"] ?: ""
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    suspend fun sendMessage(content: String) {
        if (conversationId.isNullOrBlank()){
            val createResult = messageRepository.createConversation(ReactiveStore<User>().item.value!!.id,_post.value!!.userID,_post.value!!.id)
            createResult.fold(
                ifLeft = { createError ->
                    Log.e("ChatView", "Lỗi tạo cuộc trò chuyện: ${createError.message}")

                },
                ifRight = { newConversation ->
                    conversationId = newConversation.id
                }
            )
        }
        val sendMessage = messageRepository.sendNewMessage(conversationId,content)
        sendMessage.fold (
            {error->
                Log.e("ChatView", "Lỗi gửi tin nhắn: ${error.error}")
            },
            {message ->
                _state.update { it.copy(messages = it.messages + message) }
            }
        )

    }

    fun showLoading() {
        _state.update { it.copy(isLoading = true) }
    }

    fun hideLoading() {
        _state.update { it.copy(isLoading = false) }
    }

    fun getMessages() {
        if(conversationId.isNotBlank()){ viewModelScope.launch {
            showLoading()

            val result = messageRepository.getLatestMessage(conversationId,10)
            val getConversation = messageRepository.getConversationByID(conversationId)
            getConversation.fold (
                { error ->
                    _state.update {
                        Log.e("ChatHome", "Lỗi lấy post: ${error.message}")
                        it.copy(
                            isLoading = false,
                            messages = emptyList(),
                            error = error.message ?: "Lỗi không xác định"

                        )
                    }
                },
                {conversation ->
                    val getPost = postRepository.getPostByID((conversation.postId))
                    getPost.fold(
                        { error ->
                            _state.update {
                                Log.e("ChatHome", "Lỗi lấy post: ${error.message}")
                                it.copy(
                                    isLoading = false,
                                    messages = emptyList(),
                                    error = error.message ?: "Lỗi không xác định"

                                )
                            }
                        },
                        {
                            post ->_post.value = post
                        }
                    )
                }
            )
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
        else {
            _post.value = ReactiveStore<Post>().item.value!!
        }

    }
}
