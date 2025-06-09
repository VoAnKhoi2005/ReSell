package com.example.myapplication.ui.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.MyRepository
import com.example.myapplication.ui.components.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import util.Event

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val myRepository: MyRepository
) : ViewModel(){

}