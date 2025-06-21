package com.example.resell.ui.viewmodel.chat

import com.example.resell.model.Conversation

data class ChatHomeViewState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val error: String?= null
)
