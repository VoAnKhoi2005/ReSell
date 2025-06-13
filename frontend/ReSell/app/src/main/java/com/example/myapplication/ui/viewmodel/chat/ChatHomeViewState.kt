package com.example.myapplication.ui.viewmodel.chat

import model.Conversation

data class ChatHomeViewState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val error: String?= null
)
