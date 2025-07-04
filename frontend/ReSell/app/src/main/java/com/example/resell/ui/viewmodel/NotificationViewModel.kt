package com.example.resell.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.resell.model.Notification
import com.example.resell.model.NotificationType
import com.example.resell.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = mutableStateListOf<Notification>()
    val notifications: List<Notification> get() = _notifications

    private var currentPage = 1
    private val batchSize = 20

    fun loadNextBatch() {
        viewModelScope.launch {
            when (val result = repository.getNotificationsByBatch(batchSize, currentPage)) {
                is Either.Right -> {
                    val newItems = result.value.notifications
                    _notifications.addAll(newItems)
                    currentPage++
                }
                is Either.Left -> {
                    return@launch
                }
            }
        }
    }

    fun reloadAll() {
        currentPage = 1
        _notifications.clear()
        loadNextBatch()
    }

    fun markAsRead(notification: Notification) {
        val index = _notifications.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            _notifications[index] = notification.copy(isRead = true)
        }
        // Optional: call API to mark as read
    }

    fun filterByDate(date: LocalDate) {
        viewModelScope.launch {
            when (val result = repository.getNotificationsByDate(date)) {
                is Either.Right -> {
                    _notifications.clear()
                    _notifications.addAll(result.value)
                }
                is Either.Left -> {
                    // TODO: Handle error
                }
            }
        }
    }

    fun filterByType(type: NotificationType) {
        viewModelScope.launch {
            when (val result = repository.getNotificationsByType(type)) {
                is Either.Right -> {
                    _notifications.clear()
                    _notifications.addAll(result.value)
                }
                is Either.Left -> {
                    // TODO: Handle error
                }
            }
        }
    }
}