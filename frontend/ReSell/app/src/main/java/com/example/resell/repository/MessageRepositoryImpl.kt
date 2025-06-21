package com.example.resell.repository

import android.util.Log
import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.network.WebSocketManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import model.Conversation
import model.CreateConversationRequest
import model.Message
import model.NewMessagePayload
import model.SocketMessageType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val wsManager: WebSocketManager
): MessageRepository {
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

    override suspend fun getLatestMessage(
        conversationID: String,
        amount: Int
    ): Either<NetworkError, List<Message>> {
        return Either.catch {
            apiService.getLatestMessages(conversationID, amount)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getMessageInRange(
        conversationID: String,
        start: Int,
        end: Int
    ): Either<NetworkError, List<Message>> {
        return Either.catch {
            apiService.getMessagesInRange(conversationID, start, end)
        }.mapLeft { it.toNetworkError() }
    }

    //Return temp message id before get update from server
    override suspend fun sendNewMessage(conversationID: String, content: String): Message? {
        if (!wsManager.isConnected()) {
            Log.d("WebSocket", "Fail to send: not connected")
            return null
        }

        val payload = NewMessagePayload(
            conversationID = conversationID,
            content = content
        )

        val sent = wsManager.send(SocketMessageType.NEW_MESSAGE, payload, NewMessagePayload::class.java)
        if (!sent)
            return null

        val tempId = payload.tempMessageID
        val ackDeferred = synchronized(wsManager.ackLock) {
            wsManager.ackWaiters[tempId]
        } ?: return null

        return try{
            withTimeout(5000L){
                val ack = ackDeferred.await()
                ack.message
            }
        } catch (e: TimeoutCancellationException) {
            Log.d("WebSocket", "ACK timed out for tempMessageID=$tempId")
            synchronized(wsManager.ackLock) {
                wsManager.ackWaiters.remove(tempId)
            }
            null
        }

    }
}