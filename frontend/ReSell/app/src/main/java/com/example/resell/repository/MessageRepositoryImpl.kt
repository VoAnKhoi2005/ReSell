package com.example.resell.repository

import android.util.Log
import arrow.core.Either
import com.example.resell.model.AckResult
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.store.WebSocketManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import com.example.resell.model.Conversation
import com.example.resell.model.ConversationStatDTO
import com.example.resell.model.CreateConversationRequest
import com.example.resell.model.ErrorPayload
import com.example.resell.model.GetConversationByPostAndUserResponse
import com.example.resell.model.GetLatestMessagesByBatchResponse
import com.example.resell.model.InChatIndicatorPayload
import com.example.resell.model.Message
import com.example.resell.model.NewMessagePayload
import com.example.resell.model.SocketMessageType
import com.example.resell.model.TypingIndicatorPayload
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val wsManager: WebSocketManager
): MessageRepository {
    init {
        observeTypingEvents()
        observeIncomingMessages()
    }

    override suspend fun createConversation(
        buyerID: String,
        sellerID: String,
        postID: String
    ): Either<NetworkError, Conversation> {
        return Either.catch {
            val request = CreateConversationRequest(
                buyerID = buyerID,
                sellerID = sellerID,
                postID = postID
            )

            apiService.createConversation(request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getConversationByID(conversationID: String): Either<NetworkError, Conversation> {
        return Either.catch {
            apiService.getConversationByID(conversationID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteConversation(conversationID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteConversation(conversationID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getConversationsByPostID(postID: String): Either<NetworkError, List<Conversation>> {
        return Either.catch {
            apiService.getConversationByPostID(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getAllUserConversations(): Either<NetworkError, List<Conversation>> {
        return Either.catch {
            apiService.getAllUserConversations()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getConversationByPostAndUserID(postID: String): Either<NetworkError, GetConversationByPostAndUserResponse> {
        return Either.catch {
            apiService.getConversationByPostAndUser(postID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getAllUserConversationsDTO(): Either<NetworkError, List<ConversationStatDTO>> {
        return Either.catch {
            apiService.getAllUserConversationsDTO()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getLatestMessages(
        conversationID: String,
        amount: Int
    ): Either<NetworkError, List<Message>> {
        return Either.catch {
            apiService.getLatestMessages(conversationID, amount)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getLatestMessagesByBatch(
        conversationID: String,
        batchSize: Int,
        page: Int
    ): Either<NetworkError, GetLatestMessagesByBatchResponse> {
        return Either.catch {
            apiService.getLatestMessagesByBatch(conversationID, batchSize, page)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun sendNewMessage(conversationID: String, content: String): Either<ErrorPayload, Message> {
        if (!wsManager.isConnected()) {
            Log.d("WebSocket", "Fail to send message: not connected")
            return Either.Left(ErrorPayload(error = "WebSocket not connected"))
        }

        val payload = NewMessagePayload(
            conversationID = conversationID,
            content = content
        )

        val sent = wsManager.send(SocketMessageType.NEW_MESSAGE, payload, NewMessagePayload::class.java)
        if (!sent) {
            return Either.Left(ErrorPayload(error = "Failed to send message"))
        }

        val tempId = payload.tempMessageID
        val ackDeferred = synchronized(wsManager.ackLock) {
            wsManager.ackWaiters[tempId]
        } ?: return Either.Left(ErrorPayload(error = "ACK not registered"))

        return try {
            withTimeout(10000L) {
                when (val ack = ackDeferred.await()) {
                    is AckResult.Success -> Either.Right(ack.payload.message)
                    is AckResult.Error -> Either.Left(ack.error)
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.d("WebSocket", "ACK timed out for tempMessageID=$tempId")
            synchronized(wsManager.ackLock) {
                wsManager.ackWaiters.remove(tempId)
            }
            Either.Left(ErrorPayload(error = "ACK timeout", tempMessageID = tempId))
        } catch (e: Exception) {
            Log.d("WebSocket", "Unexpected error: ${e.message}")
            Either.Left(ErrorPayload(error = e.message ?: "Unknown error", tempMessageID = tempId))
        }
    }

    override suspend fun sendInChatIndicator(
        conversationID: String,
        isInChat: Boolean
    ): Boolean {
        if (!wsManager.isConnected()) {
            Log.d("WebSocket", "Fail to send in chat indicator: not connected")
            return false
        }

        val payload = InChatIndicatorPayload(
            conversationID = conversationID,
            isInChat = isInChat,
        )

        val sent = wsManager.send(SocketMessageType.IN_CHAT, payload, InChatIndicatorPayload::class.java)
        if (!sent) {
            Log.d("WebSocket", "Failed to send in chat indicator")
            return false
        }

        val tempId = payload.tempMessageID
        val ackDeferred = synchronized(wsManager.ackLock) {
            wsManager.ackWaiters[tempId]
        } ?: return false

        return try{
            withTimeout(5000L) {
                when (val ack = ackDeferred.await()) {
                    is AckResult.Error -> {
                        Log.d("WebSocket", "Received error ACK for in-chat: ${ack.error.error}")
                        false
                    }
                    else -> true
                }
            }
        } catch (e: TimeoutCancellationException) {
            synchronized(wsManager.ackLock) {
                wsManager.ackWaiters.remove(tempId)
            }
            return true
        }
    }

    override suspend fun sendTypingIndicator(conversationID: String, userID: String, isTyping: Boolean) {
        if (!wsManager.isConnected()) {
            Log.d("WebSocket", "Fail to send in chat indicator: not connected")
            return
        }

        val payload = TypingIndicatorPayload(
            conversationID = conversationID,
            userID = userID,
            isTyping = isTyping,
        )

        val sent = wsManager.send(SocketMessageType.TYPING, payload, TypingIndicatorPayload::class.java)
        if (!sent) {
            Log.d("WebSocket", "Failed to send typing indicator")
            return
        }
    }

    private val _isTyping = MutableStateFlow(false)
    override val isTyping: StateFlow<Boolean> = _isTyping
    private var typingJob: Job? = null

    private fun observeTypingEvents() {
        CoroutineScope(Dispatchers.Default).launch {
            wsManager.typingEvents.collect { payload ->
                if (payload.isTyping) {
                    _isTyping.value = true

                    typingJob?.cancel()
                    typingJob = CoroutineScope(Dispatchers.Default).launch {
                        delay(5000)
                        _isTyping.value = false
                    }
                } else {
                    typingJob?.cancel()
                    _isTyping.value = false
                }
            }
        }
    }

    private val _receivedMessage = MutableSharedFlow<Message>()
    override val receivedMessage: SharedFlow<Message> = _receivedMessage

    private fun observeIncomingMessages() {
        CoroutineScope(Dispatchers.Default).launch {
                wsManager.messageEvents.collect { payload ->
                val message = payload.message
                val currentUserId = ReactiveStore<User>().item.value?.id

                if (message.senderId != currentUserId) {
                    _receivedMessage.emit(message)
                }
            }
        }
    }
}