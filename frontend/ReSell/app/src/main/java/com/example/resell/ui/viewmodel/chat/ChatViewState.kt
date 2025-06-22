package com.example.resell.ui.viewmodel.chat

import com.example.resell.model.Message

data class ChatViewState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val error: String?= null
)
