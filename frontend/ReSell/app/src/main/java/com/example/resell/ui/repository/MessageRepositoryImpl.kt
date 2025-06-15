package com.example.resell.ui.repository

import android.util.Log
import arrow.core.Either
import com.example.resell.ui.network.ApiService
import com.example.resell.ui.domain.NetworkError
import com.example.resell.ui.mapper.toNetworkError
import com.example.resell.ui.network.WebSocketManager
import model.Conversation
import model.CreateConversationRequest
import model.Message
import model.SendMessagePayload
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
    override suspend fun sendWSMessage(conversationID: String, content: String): String {
        if (!wsManager.isConnected()) {
            Log.d("WebSocket", "Fail to send: not connected")
            return ""
        }

        val payload = SendMessagePayload(
            conversationID = conversationID,
            content = content
        )

        return wsManager.send(SocketMessageType.new_message, payload, SendMessagePayload::class.java)
    }
}