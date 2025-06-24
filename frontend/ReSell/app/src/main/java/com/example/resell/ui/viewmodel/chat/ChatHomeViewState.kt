package com.example.resell.ui.viewmodel.chat

import com.example.resell.model.Conversation
import com.example.resell.model.Post
import com.example.resell.model.User

data class ChatHomeViewState(
    val isLoading: Boolean = false,
    val conversationCards: List<ConversationCard> = emptyList(),
    val error: String?= null
)
data class ConversationCard(
    val conversation: Conversation,
    val post: Post
)