package com.example.resell.store

import com.squareup.moshi.Moshi
import com.example.resell.model.SocketMessage
import com.example.resell.model.TypingIndicatorPayload
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.resell.model.AckResult
import com.squareup.moshi.Types
import kotlinx.coroutines.CompletableDeferred
import com.example.resell.model.ErrorPayload
import com.example.resell.model.NewMessagePayload
import com.example.resell.model.PendingMessage
import com.example.resell.model.SendMessagePayload
import com.example.resell.model.SocketMessageType
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val tokenManager: AuthTokenManager
){
    private val url = "ws://localhost:8080/api/ws"
    private var webSocket: WebSocket? = null
    private val pendingMessages = mutableMapOf<String, PendingMessage>()
    private val queueLock = Any()

    val ackWaiters = mutableMapOf<String, CompletableDeferred<AckResult>>()
    val ackLock = Any()

    private val _typingEvents = MutableSharedFlow<TypingIndicatorPayload>()
    val typingEvents: SharedFlow<TypingIndicatorPayload> = _typingEvents

    suspend fun connect(): Boolean {
        val jwt = tokenManager.getAccessToken()
        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $jwt").build()
        val connectionResult = CompletableDeferred<Boolean>()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected to $url")
                connectionResult.complete(true)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                try {
                    val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
                    val socketMessageType = Types.newParameterizedType(SocketMessage::class.java, mapType)

                    val rawAdapter = moshi.adapter<SocketMessage<Map<String, Any>>>(socketMessageType)
                    val raw = rawAdapter.fromJson(text) ?: return

                    when (raw.type) {
                        SocketMessageType.TYPING -> {
                            val data = parseSocketMessageData<TypingIndicatorPayload>(raw.type, raw.data, moshi)
                            data?.let { _typingEvents.tryEmit(it) }
                        }

                        SocketMessageType.SEND_MESSAGE -> {
                            val data = parseSocketMessageData<SendMessagePayload>(raw.type, raw.data, moshi)
                            val tempId = data?.tempMessageID

                            if (tempId != null) {
                                synchronized(queueLock) {
                                    pendingMessages.remove(tempId)
                                }
                                synchronized(ackLock) {
                                    ackWaiters.remove(tempId)?.complete(AckResult.Success(data))
                                }
                            }
                        }

                        SocketMessageType.ERROR -> {
                            val data = parseSocketMessageData<ErrorPayload>(raw.type, raw.data, moshi)
                            val tempId = data?.tempMessageID

                            if (tempId != null) {
                                synchronized(ackLock) {
                                    ackWaiters.remove(tempId)?.complete(AckResult.Error(data))
                                }
                            }
                        }

                        else -> Log.w("WebSocket", "Unknown type: ${raw.type}")
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing WebSocket message", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Failure: ${t.message}", t)
                connectionResult.complete(false)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closed: $reason")
            }
        })

        return connectionResult.await()
    }

    fun <T> send(type: SocketMessageType, payload: T, payloadClass: Class<T>): Boolean {
        val message = SocketMessage(type, payload)
        val typeToken = Types.newParameterizedType(SocketMessage::class.java, payloadClass)
        val adapter = moshi.adapter<SocketMessage<T>>(typeToken)

        if (adapter == null) {
            println("Failed to create Moshi adapter for type: ${payloadClass.name}")
            return false
        }

        val json = adapter.toJson(message)

        val isSent = webSocket?.send(json) == true
        if (type == SocketMessageType.NEW_MESSAGE) {
            val tempId = try {
                val prop = payload!!::class.members.firstOrNull { it.name == "tempMessageID" }
                prop?.call(payload) as? String
            } catch (e: Exception) {
                null
            } ?: UUID.randomUUID().toString()

            val pending = PendingMessage(id = tempId, json = json, raw = payload as Any)
            synchronized(queueLock) {
                pendingMessages[pending.id] = pending
            }

            synchronized(ackLock) {
                ackWaiters[tempId] = CompletableDeferred()
            }
        }

        return isSent
    }

    fun retryMessage(id: String): Boolean {
        synchronized(queueLock) {
            val pending = pendingMessages[id] ?: return false
            val sent = webSocket?.send(pending.json) == true

            if (sent) {
                pendingMessages.remove(id)
            } else {
                Log.w("WebSocket", "Retry failed again: $id")
            }
            return sent
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
    }

    fun isConnected(): Boolean {
        return webSocket != null
    }
}

inline fun <reified T> parseSocketMessageData(
    type: SocketMessageType,
    data: Map<String, Any>,
    moshi: Moshi
): T? {
    val socketType = Types.newParameterizedType(SocketMessage::class.java, T::class.java)
    val adapter = moshi.adapter<SocketMessage<T>>(socketType)

    val fullMap = mapOf(
        "type" to type.name,
        "data" to data
    )

    return adapter.fromJsonValue(fullMap)?.data
}