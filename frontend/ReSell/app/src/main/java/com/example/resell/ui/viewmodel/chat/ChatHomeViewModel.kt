package com.example.resell.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.resell.model.Conversation
import com.example.resell.model.Message
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val myRepository: MessageRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatHomeViewState())
    val state  = _state.asStateFlow()
    init{
        getConversations()
    }

    private fun getConversations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = myRepository.getAllConversations()

            result.fold(
                { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            conversations = emptyList(),
                            error = error.message ?: "Lỗi không xác định"
                        )
                    }
                },
                { conversations ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            conversations = conversations,
                            error = null
                        )
                    }
                }
            )
        }
    }
}

