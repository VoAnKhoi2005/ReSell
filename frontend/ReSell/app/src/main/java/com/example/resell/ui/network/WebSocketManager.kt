package com.example.resell.ui.network

import com.squareup.moshi.Moshi
import model.Message
import model.IncomingSocketMessage
import model.SendMessagePayload
import model.SocketMessage
import model.TypingPayload
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.squareup.moshi.Types
import model.MessageStatus
import model.PendingMessage
import model.SocketMessageType
import store.TokenManager

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val tokenManager: TokenManager
){
    private var webSocket: WebSocket? = null
    private var listener: ((Any) -> Unit)? = null
    private val pendingMessages = mutableListOf<PendingMessage>()
    private val queueLock = Any()

    private val rawAdapter = moshi.adapter(IncomingSocketMessage::class.java)

    fun connect(url: String, onMessage: (Any) -> Unit) {
        val jwt = tokenManager.getAccessToken()
        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $jwt").build()
        this.listener = onMessage

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected to $url")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                try {
                    val raw = rawAdapter.fromJson(text) ?: return
                    when (raw.type) {
                        "new_message" -> {
                            val adapter = moshi.adapter(Message::class.java)
                            val json = moshi.adapter(Map::class.java).toJsonValue(raw.data)
                            val payload = adapter.fromJsonValue(json)
                            payload?.let { listener?.invoke(it) }
                        }

                        "typing" -> {
                            val adapter = moshi.adapter(TypingPayload::class.java)
                            val json = moshi.adapter(Map::class.java).toJsonValue(raw.data)
                            val payload = adapter.fromJsonValue(json)
                            payload?.let { listener?.invoke(it) }
                        }

                        "message_send" -> {
                            val adapter = moshi.adapter(Message::class.java)
                            val json = moshi.adapter(Map::class.java).toJsonValue(raw.data)
                            val payload = adapter.fromJsonValue(json)
                            payload?.let { listener?.invoke(it) }
                        }

                        else -> Log.w("WebSocket", "Unknown type: ${raw.type}")
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing WebSocket message", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Failure: ${t.message}", t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closed: $reason")
            }
        })
    }

    fun <T> send(type: SocketMessageType, payload: T, payloadClass: Class<T>, trackFailure: Boolean = true): String {
        val message = SocketMessage(type, payload)
        val typeToken = Types.newParameterizedType(SocketMessage::class.java, payloadClass)
        val adapter = moshi.adapter<SocketMessage<T>>(typeToken)
        val json = adapter.toJson(message)

        Log.d("WebSocket", "Sending: $json")
        val sent = webSocket?.send(json) == true
        if (sent){
            Log.d("WebSocket", "Sent: $json")
            return ""
        } else {
            if (trackFailure){
                val pending = PendingMessage(json = json, raw = payload as Any)
                synchronized(queueLock) {
                    pendingMessages.add(pending)
                }
                return pending.id
            } else {
                return ""
            }
        }
    }

    fun retryMessage(id: String): Boolean {
        synchronized(queueLock) {
            val pending = pendingMessages.find { it.id == id } ?: return false
            val sent = webSocket?.send(pending.json) == true
            if (sent) {
                pending.status = MessageStatus.SENT
                pendingMessages.remove(pending)
                Log.d("WebSocket", "Retry success for message: $id")
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