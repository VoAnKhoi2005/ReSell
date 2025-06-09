package com.example.myapplication.ui.view.test

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.MyRepository
import com.example.myapplication.ui.components.sendEvent
import com.example.myapplication.ui.viewmodel.login.LoginViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import util.Event
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
@HiltViewModel
class PostViewModel @Inject constructor(
    private val myRepository: MyRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PostViewState())
    val state = _state.asStateFlow()
    init {
        getPosts()
    }
    fun getPosts(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = myRepository.getPosts()

            result.fold(
                ifLeft = { error ->
                    _state.update { it.copy(error = error.error.message) }
                    sendEvent(Event.Toast(error.error.message))
                },
                ifRight = { posts ->
                    _state.update { it.copy(posts = posts) }
                }
            )

            _state.update { it.copy(isLoading = false) }
            Log.d("Test","Hereeeeeeeeee")
        }

    }
}