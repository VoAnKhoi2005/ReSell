package com.example.resell.ui.viewmodel


import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.resell.repository.PostRepository


import com.example.resell.ui.navigation.NavigationController

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val repository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- Input ---
    val postId: String = savedStateHandle["postId"] ?: ""

    private val _existingImageUrls = mutableStateOf<List<String>>(emptyList())
    val existingImageUrls: State<List<String>> = _existingImageUrls

    private val _newImageUris = mutableStateOf<List<Uri>>(emptyList())
    val newImageUris: State<List<Uri>> = _newImageUris

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _priceText = mutableStateOf("")
    val priceText: State<String> = _priceText

    private val _addressName = mutableStateOf("")
    val addressName: State<String> = _addressName

    private val _categoryName = mutableStateOf("")
    val categoryName: State<String> = _categoryName

    // --- Error states ---
    val titleError = mutableStateOf("")
    val descriptionError = mutableStateOf("")
    val priceError = mutableStateOf("")
    val addressError = mutableStateOf("")
    val categoryError = mutableStateOf("")

    // --- Loading ---
    val isLoading = mutableStateOf(false)

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            val post = repository.getPostByID(postId)
            post.fold(
                {
                    Log.e("ccccd",it.message?:"")
                },{
                    _existingImageUrls.value = it.images?.map { image -> image.url } ?:emptyList()
                    _title.value = it.title
                    _description.value = it.description?:""
                    _priceText.value = it.price.toString()
                    _addressName.value =listOfNotNull(it.ward?.name,it?.ward?.district?.name,it?.ward?.district?.province?.name).joinToString(", ")
                    _categoryName.value = it.category?.name?:""
                }
            )
        }
    }

    fun addNewImages(uris: List<Uri>) {
        val availableSlots = 6 - (_existingImageUrls.value.size + _newImageUris.value.size)
        _newImageUris.value = _newImageUris.value + uris.take(availableSlots)
    }

    fun removeExistingImageAt(index: Int) {
        _existingImageUrls.value = _existingImageUrls.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun removeNewImageAt(index: Int) {
        _newImageUris.value = _newImageUris.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
        titleError.value = if (newTitle.length > 50) "Tối đa 50 ký tự" else ""
    }

    fun onDescriptionChange(newDesc: String) {
        _description.value = newDesc
        descriptionError.value = if (newDesc.length > 1500) "Tối đa 1500 ký tự" else ""
    }

    fun onPriceChange(newPrice: String) {
        _priceText.value = newPrice
        priceError.value = when {
            newPrice.isBlank() -> "Vui lòng nhập giá"
            newPrice.toLongOrNull() == null -> "Giá không hợp lệ"
            newPrice.toLong() > 100_000_000 -> "Tối đa 100 triệu"
            else -> ""
        }
    }

    fun setAddress(address: String) {
        _addressName.value = address
        addressError.value = ""
    }

    fun setCategory(name: String) {
        _categoryName.value = name
        categoryError.value = ""
    }

    val isReadyToSubmit: Boolean
        get() = _title.value.isNotBlank()
                && _description.value.isNotBlank()
                && _priceText.value.toLongOrNull() != null
                && _addressName.value.isNotBlank()
                && _categoryName.value.isNotBlank()
                && (_existingImageUrls.value + _newImageUris.value).isNotEmpty()

    fun submitPost() {
        viewModelScope.launch {
            isLoading.value = true
            delay(1000) // Mô phỏng upload + API gọi
            // TODO: gọi repository.updatePost(...) tại đây
            isLoading.value = false
            NavigationController.navController.popBackStack()
        }
    }
}