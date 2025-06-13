package com.example.myapplication.ui.viewmodel.chat

import model.Message

data class ChatViewState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val error: String?= null
)
