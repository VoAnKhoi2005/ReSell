package com.example.resell.ui.viewmodel.chat

import android.R.id.message
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Message
import com.example.resell.model.Post
import com.example.resell.model.User
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.store.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val user = ReactiveStore<User>().item.value

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _listMessages = MutableStateFlow<List<Message>>(emptyList())
    val listMessages: StateFlow<List<Message>> = _listMessages

    var conversationId: String = savedStateHandle["conversationId"] ?: ""
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val batchSize = 20
    var isMoreMessage : Boolean = false
    private var currentBatch: Int = 1

    private val _incomingMessage = MutableSharedFlow<Message>(extraBufferCapacity = 64)
    val incomingMessage: SharedFlow<Message> = _incomingMessage

    init {
        observeMessages()
    }

    suspend fun inChat(isInChat : Boolean){
        messageRepository.sendInChatIndicator(conversationId,isInChat)
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageRepository.receivedMessage
                .filter { it.conversationId == conversationId }
                .collect { msg ->
                    _listMessages.value = listOf(msg) + _listMessages.value
                    _incomingMessage.emit(msg)
                }
        }
    }
    suspend fun sendMessage(content: String): Boolean {
        if (conversationId.isBlank()){
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
                _isLoading.value = false

            },
            {message ->
                _isLoading.value = false
                _listMessages.value = listOf(message) + _listMessages.value
                return true
            }
        )
        return false

    }
    suspend fun loadMoreMessages() : Int{
        Log.d("Chat", "Loadmore")
        if (!isMoreMessage) return 0;
        val result = messageRepository.getLatestMessagesByBatch(conversationId,batchSize,currentBatch)
        result.fold(
            { error ->
                _isLoading.value = false
                _listMessages.value = emptyList()
                Log.e("ChatHome", "Lỗi lấy tin nhắn: ${error.message}")
                return 0;

            },
            { response ->

                _listMessages.value =  _listMessages.value + response.messages
                if (response.totalBatchCount <= currentBatch) {
                    isMoreMessage = false
                }
                currentBatch ++
                return response.messages.size
            }
        )
    }
    fun showLoading(){
        _isLoading.value = true
    }
    fun hideLoading(){
        _isLoading.value = false
    }

    fun getMessages() {
        if(conversationId.isNotBlank()){ viewModelScope.launch {
            _isLoading.value = true


            val getConversation = messageRepository.getConversationByID(conversationId)
            getConversation.fold (
                { error ->
                    _listMessages.value = emptyList()
                    Log.e("ChatHome", "Lỗi lấy conversation: ${error.message}")
                },
                {conversation ->
                    val getPost = postRepository.getPostByID((conversation.postId))
                    getPost.fold(
                        { error ->
                            _isLoading.value = false
                            _listMessages.value = emptyList()
                            Log.e("ChatHome", "Lỗi lấy post: ${error.message}")

                        },
                        {
                            post ->_post.value = post
                        }
                    )
                }
            )
            val result = messageRepository.getLatestMessagesByBatch(conversationId,batchSize,1)
            result.fold(
                { error ->
                    _isLoading.value = false
                    _listMessages.value = emptyList()
                    Log.e("ChatHome", "Lỗi lấy tin nhắn: ${error.message}")

                },
                { response ->

                    _listMessages.value = response.messages
                    if (response.totalBatchCount <= currentBatch) {
                        isMoreMessage = false
                    }
                    else isMoreMessage = true
                    currentBatch++
                    _isLoading.value = false

                }
            )
        }
        }
        else {
            _post.value = ReactiveStore<Post>().item.value!!
        }

    }
}
