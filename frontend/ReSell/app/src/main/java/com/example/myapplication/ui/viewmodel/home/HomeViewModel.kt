package com.example.myapplication.ui.viewmodel.home
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var searchResults by mutableStateOf(listOf<String>())
        private set

    private val allItems = listOf(
        "Xe máy",
        "Điện thoại",
        "Laptop",
        "Tủ lạnh",
        "Tivi",
        "Bàn ghế"
    )

    fun onSearchTextChanged(newText: String) {
        searchText = newText
        searchResults = if (newText.isBlank()) {
            emptyList()
        } else {
            allItems.filter { it.contains(newText, ignoreCase = true) }
        }
    }
}

