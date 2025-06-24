package com.example.resell.ui.viewmodel.chat

import com.example.resell.model.Conversation
import com.example.resell.model.ConversationStatDTO
import com.example.resell.model.Post
import com.example.resell.model.User
import com.squareup.moshi.Json
import java.time.LocalDateTime

data class ChatHomeViewState(
    val isLoading: Boolean = false,
    val conversationCards: List<ConversationStatDTO> = emptyList(),
    val error: String?= null
)
