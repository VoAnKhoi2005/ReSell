package com.example.resell.ui.viewmodel.addpost

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.model.Category
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.Ward
import com.example.resell.repository.PostRepository
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val repository: PostRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priceText by mutableStateOf("")
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    var titleError by mutableStateOf("")
    var descriptionError by mutableStateOf("")
    var priceError by mutableStateOf("")
    var addressError by mutableStateOf("")
    var categoryError by mutableStateOf("")

    val imageUrls = mutableStateListOf<Uri>()

    var selectedProvince by mutableStateOf<Province?>(null)
    var selectedDistrict by mutableStateOf<District?>(null)
    var selectedWard by mutableStateOf<Ward?>(null)

    var selectedCategory by mutableStateOf<Category?>(null)

    val addressName: String
        get() = listOfNotNull(selectedWard?.name,selectedDistrict?.name,selectedProvince?.name).joinToString(", ")

    var categoryName by mutableStateOf("")

    val isReadyToSubmit: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && priceText.toLongOrNull()?.let { it <= 100_000_000 } == true && imageUrls.isNotEmpty() && selectedProvince != null && selectedDistrict != null && selectedWard != null && selectedCategory != null

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

    fun addImages(uris: List<Uri>) {
        val availableSlots = 6 - imageUrls.size
        imageUrls.addAll(uris.take(availableSlots))
    }

    fun removeImageAt(index: Int) {
        if (index in imageUrls.indices) imageUrls.removeAt(index)
    }

    fun setCategory(category: Category?,name: String?) {
        categoryName = name?:""
        selectedCategory = category
        categoryError = ""
    }

    fun setAddress(province: Province?, district: District?, ward: Ward?) {
        selectedProvince = province
        selectedDistrict = district
        selectedWard = ward
        addressError = ""
    }

    suspend fun submitPost() {
        validateInputs()
        if (!isReadyToSubmit) return
        _isLoading.value = true
        val post = repository.createPost(title,description,selectedCategory?.id?:"",selectedWard?.id?:"",priceText.toInt())
        post.fold(
            {
                Log.e("Create Post",it.message?:"Lỗi tạo post")
                _isLoading.value = false
            },
            {
                val files = imageUrls.mapNotNull { uri -> uriToFile(appContext, uri) }
                Log.d("Số ảnh: ","${files.size}")
                val result = repository.uploadPostImage(it.id, files)

                result.fold(
                    ifLeft = { error ->
                        Log.e("Post Image",error.message?:"Đăng ảnh")
                        _isLoading.value = false
                    },
                    ifRight = { success->
                        _isLoading.value = false
                        EventBus.sendEvent(Event.Toast("Đăng bài thành công"))
                        NavigationController.navController.navigate(Screen.Main.route)
                    }
                )
            }
        )

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

    private fun validateInputs() {
        if (title.isBlank()) titleError = "Không được để trống tiêu đề"
        if (description.isBlank()) descriptionError = "Không được để trống mô tả"
        if (priceText.isBlank()) priceError = "Không được để trống giá"
        else if (priceText.toLongOrNull()?.let { it > 100_000_000 } == true) priceError = "Giá tối đa là 100 triệu"
        if (imageUrls.isEmpty()) imageUrls.clear() // không lỗi text
        if (selectedProvince == null || selectedDistrict == null || selectedWard == null) addressError = "Vui lòng chọn đầy đủ địa chỉ"
        if (selectedCategory == null) categoryError = "Vui lòng chọn danh mục"
    }
}