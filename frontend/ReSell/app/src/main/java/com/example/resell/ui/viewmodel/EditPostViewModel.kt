package com.example.resell.ui.viewmodel


import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.example.resell.model.Category
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.UpdatePostRequest
import com.example.resell.model.Ward
import com.example.resell.repository.PostRepository


import com.example.resell.ui.navigation.NavigationController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val repository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _deletedImageUrls = mutableStateOf<List<String>>(emptyList())
    val deletedImageUrls: State<List<String>> = _deletedImageUrls
    val postId: String = savedStateHandle["id"] ?: ""

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priceText by mutableStateOf("")
    val imageUrls = mutableStateListOf<Uri>()
    val existingImageUrls = mutableStateListOf<String>()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var titleError by mutableStateOf("")
    var descriptionError by mutableStateOf("")
    var priceError by mutableStateOf("")
    var addressError by mutableStateOf("")
    var categoryError by mutableStateOf("")

    var selectedProvince by mutableStateOf<Province?>(null)
    var selectedDistrict by mutableStateOf<District?>(null)
    var selectedWard by mutableStateOf<Ward?>(null)
    var selectedCategory by mutableStateOf<Category?>(null)
    var categoryName by mutableStateOf("")

    val addressName: String
        get() = listOfNotNull(selectedWard?.name, selectedDistrict?.name, selectedProvince?.name).joinToString(", ")

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            val result = repository.getPostByID(postId)
            result.fold(
                { Log.e("EditPost", it.message ?: "") },
                {
                    title = it.title
                    description = it.description ?: ""
                    priceText = it.price.toString()
                    selectedWard = it.ward
                    selectedDistrict = it.ward?.district
                    selectedProvince = it.ward?.district?.province
                    selectedCategory = it.category
                    categoryName = it.category?.name ?: ""
                    existingImageUrls.clear()
                    existingImageUrls.addAll(it.images?.map { img -> img.url } ?: emptyList())
                }
            )
        }
    }

    fun onTitleChange(newValue: String) {
        title = newValue.take(50)
        titleError = if (title.isBlank()) "Không được để trống tiêu đề" else ""
    }

    fun onDescriptionChange(newValue: String) {
        description = newValue.take(1500)
        descriptionError = if (description.isBlank()) "Không được để trống mô tả" else ""
    }

    fun onPriceChange(newValue: String) {
        priceText = newValue.filter { it.isDigit() }
        priceError = when {
            priceText.isBlank() -> "Không được để trống giá"
            priceText.toLongOrNull()?.let { it > 100_000_000 } == true -> "Giá tối đa là 100 triệu"
            else -> ""
        }
    }

    fun setCategory(category: Category?, name: String?) {
        selectedCategory = category
        categoryName = name ?: ""
        categoryError = ""
    }

    fun setAddress(province: Province?, district: District?, ward: Ward?) {
        selectedProvince = province
        selectedDistrict = district
        selectedWard = ward
        addressError = ""
    }

    fun addNewImages(uris: List<Uri>) {
        val availableSlots = 6 - (existingImageUrls.size + imageUrls.size)
        imageUrls.addAll(uris.take(availableSlots))
    }

    fun removeExistingImageAt(index: Int) {
        val removedUrl = existingImageUrls.removeAt(index)
        _deletedImageUrls.value = _deletedImageUrls.value + removedUrl
    }

    fun removeNewImageAt(index: Int) {
        if (index in imageUrls.indices) {
            imageUrls.removeAt(index)
        }
    }

    fun validateInputs() {
        if (title.isBlank()) titleError = "Không được để trống tiêu đề"
        if (description.isBlank()) descriptionError = "Không được để trống mô tả"
        if (priceText.isBlank()) priceError = "Không được để trống giá"
        else if (priceText.toLongOrNull()?.let { it > 100_000_000 } == true) priceError = "Giá tối đa là 100 triệu"
        if (existingImageUrls.isEmpty() && imageUrls.isEmpty()) imageUrls.clear()
        if (selectedProvince == null || selectedDistrict == null || selectedWard == null) addressError = "Vui lòng chọn đầy đủ địa chỉ"
        if (selectedCategory == null) categoryError = "Vui lòng chọn danh mục"
    }

    val isReadyToSubmit: Boolean
        get() = title.isNotBlank()
                && description.isNotBlank()
                && priceText.toLongOrNull()?.let { it <= 100_000_000 } == true
                && (imageUrls.isNotEmpty() || existingImageUrls.isNotEmpty())
                && selectedProvince != null && selectedDistrict != null && selectedWard != null && selectedCategory != null

    fun submitPost() {
        viewModelScope.launch {
            validateInputs()
            if (!isReadyToSubmit) return@launch
            _isLoading.value = true

            repository.deletePostImages(postId,_deletedImageUrls.value)
            val postUpdate = UpdatePostRequest(
                categoryID = selectedCategory?.id,
                wardID = selectedWard?.id,
                title = title,
                description = description,
                price = priceText.toIntOrNull()
            )
            repository.updatePost(postId,postUpdate)
         //   repository.uploadPostImage(postId,imageUrls)
            _isLoading.value = false
            NavigationController.navController.popBackStack()
        }
    }
}
private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
