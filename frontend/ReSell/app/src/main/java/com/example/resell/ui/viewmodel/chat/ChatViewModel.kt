package com.example.resell.ui.viewmodel.chat

import android.R.id.message
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.some
import com.example.resell.R
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import com.example.resell.model.Post
import com.example.resell.model.User
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.PostRepository
import com.example.resell.store.ReactiveStore
import com.example.resell.store.WebSocketManager
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.chat.chatscreen.SellPopupState
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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

    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()

    private val _isSeller = MutableStateFlow<Boolean>(false)
    val isSeller: StateFlow<Boolean> = _isSeller.asStateFlow()

    var receiverUsername :String =""
    private val batchSize = 20
    var isMoreMessage : Boolean = false
    private var currentBatch: Int = 1

    private val _incomingMessage = MutableSharedFlow<Message>(extraBufferCapacity = 64)
    val incomingMessage: SharedFlow<Message> = _incomingMessage
    val showSellPopup = MutableStateFlow(false)
    val sellPopupState = MutableStateFlow(SellPopupState.EditPrice)

    fun showEditPricePopup() {
        sellPopupState.value = SellPopupState.EditPrice
        showSellPopup.value = true
    }

    fun showConfirmPopup() {
        sellPopupState.value = SellPopupState.ConfirmSelling
        showSellPopup.value = true
        Log.d("Show","Sell")
    }
    fun onBuyClick(){
        ReactiveStore<Post>().set(_post.value)
        ReactiveStore<Conversation>().set(_conversation.value)
        NavigationController.navController.navigate(Screen.Payment.route)
    }
    fun hideSellPopup() {
        showSellPopup.value = false
    }

    fun onChangePrice(price: Int) {
        viewModelScope.launch {
            val offer = messageRepository.updateOffer(
                conversationID = conversation.value?.id?:"",
                isSelling = true,
                amount = price
            )
            offer.fold(
                {
                    Log.e("Mở bán",it.message?:"")
                },{
                    _conversation.value = it
                    val systemKey : String ="system5cbb2e3d8819ef6c1769e55"
                    sendMessage(systemKey+" ${user?.fullName} đã mở bán với giá ${price}VND")
                }
            )
        }
    }

    fun onConfirmSell(price: Int) {
        viewModelScope.launch {
            val offer = messageRepository.updateOffer(
                conversationID = conversation.value?.id?:"",
                isSelling = true,
                amount = price
            )
            offer.fold(
                {
                    Log.e("Sửa giá",it.message?:"")
                },{
                    _conversation.value = it
                    val systemKey : String ="system5cbb2e3d8819ef6c1769e55"
                    sendMessage(systemKey+" ${user?.fullName} đã sửa giá thành ${price}VND")
                }
            )
        }
        hideSellPopup()
    }

    fun onCancelSell() {
        viewModelScope.launch {
            val offer = messageRepository.updateOffer(
                conversationID = conversation.value?.id?:"",
                isSelling = false
            )
            offer.fold(
                {
                    Log.e("Hủy bán",it.message?:"")
                },{
                    _conversation.value = it
                    val systemKey : String ="system5cbb2e3d8819ef6c1769e55"
                    sendMessage(systemKey+" ${user?.fullName} đã hủy bán")
                }
            )
        }
        hideSellPopup()
    }

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
    fun senddImage(file: File) {
        val imageKey : String ="dab64614f35cbb2e3d8819ef6c1769e55"
        viewModelScope.launch {
            val img = messageRepository.uploadImage(file)
            img.fold(
                {
                    error ->
                    Log.e("Chat", "Lỗi upload ảnh: ${error.message}")
                    EventBus.sendEvent(Event.Toast("Lỗi upload ảnh: ${error.message}"))
                },
                {
                    url ->
                    EventBus.sendEvent(Event.Toast("Upload: ${url}"))
                    sendMessage("${imageKey} $url")
                }
            )
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
                    val offer = messageRepository.updateOffer(
                        newConversation.id,
                        isSelling = false,
                        amount =_post.value!!.price )
                    offer.fold(
                        {
                            Log.e("ChatView", "Lỗi tạo offer: ${it.message}")
                        },{it->
                            conversationId = it.id
                            _conversation.value = it
                        }
                    )
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
                    _conversation.value =conversation
                    receiverUsername = if (ReactiveStore<User>().item.value!!.username == conversation.seller!!.username) {
                        _isSeller.value = true
                        conversation.buyer!!.fullName

                    }else {
                        _isSeller.value = false
                        conversation.seller.fullName
                    }
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
            receiverUsername = _post.value?.user?.fullName?:""
            _isSeller.value = false
        }

    }
}
