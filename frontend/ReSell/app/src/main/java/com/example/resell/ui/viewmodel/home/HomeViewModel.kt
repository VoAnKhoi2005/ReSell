package com.example.resell.ui.viewmodel.home
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.resell.repository.UserRepository
import com.example.resell.ui.screen.home.ProductPost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val myRepository: UserRepository
) : ViewModel(){

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


val postList = listOf(
    ProductPost(
        title = "iPhone 14 Pro Max 256GB",
        time = "2 giờ trước",
        imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 25500000,
        category = "Điện thoại",
        address = "Quận 1, TP.HCM"
    ),
    ProductPost(
        title = "Xe máy Honda SH 2023",
        time = "5 giờ trước",
        imageUrl = "https://images.unsplash.com/photo-1609630875171-b1321377ee65?q=80&w=1960&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 75000000,
        category = "Phương tiện",
        address = "Quận 7, TP.HCM"
    ),
    ProductPost(
        title = "MacBook Pro M1 2020",
        time = "1 ngày trước",
        imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 32000000,
        category = "Laptop",
        address = "Hà Nội"
    ),
    ProductPost(
        title = "MacBook Pro M1 2020",
        time = "1 ngày trước",
        imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 32000000,
        category = "Laptop",
        address = "Hà Nội"
    ),
    ProductPost(
        title = "MacBook Pro M1 2020",
        time = "1 ngày trước",
        imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 32000000,
        category = "Laptop",
        address = "Hà Nội"
    ),
    ProductPost(
        title = "MacBook Pro M1 2020",
        time = "1 ngày trước",
        imageUrl = "https://plus.unsplash.com/premium_photo-1681702114246-ffe628203982?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        price = 32000000,
        category = "Laptop",
        address = "Hà Nội"
    )
)


