package com.example.resell.ui.screen
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.resell.ui.components.AddressPickerPopup
import com.example.resell.ui.components.CategoryPickerPopup
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.screen.add.RequiredLabel
import com.example.resell.ui.viewmodel.EditPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showAddressPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addNewImages(uris)
    }

    val existingImageUrls = viewModel.existingImageUrls.value
    val newImageUris = viewModel.newImageUris.value

    val isLoading = viewModel.isLoading.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa bài đăng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        NavigationController.navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.submitPost() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.isReadyToSubmit
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(2.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cập nhật bài đăng")
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 72.dp)
        ) {
            // Danh mục
            RequiredLabel("Danh mục")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showCategoryPicker = true }
            ) {
                OutlinedTextField(
                    value = viewModel.categoryName.value.ifBlank { "Chọn danh mục" },
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (viewModel.categoryName.value.isBlank()) Color.Gray else LocalContentColor.current,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            if (viewModel.categoryError.value.isNotBlank()) {
                Text(
                    viewModel.categoryError.value,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Ảnh
            RequiredLabel("Ảnh sản phẩm", showAsterisk = false)
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                // Nút thêm ảnh
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Ảnh từ URL
                itemsIndexed(existingImageUrls) { index, url ->
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removeExistingImageAt(index) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Xoá", tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Ảnh từ máy
                itemsIndexed(newImageUris) { index, uri ->
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removeNewImageAt(index) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Xoá", tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tiêu đề
            RequiredLabel("Tiêu đề")
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.onTitleChange(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tối đa 50 ký tự") },
                isError = viewModel.titleError.value.isNotBlank()
            )
            if (viewModel.titleError.value.isNotBlank()) {
                Text(viewModel.titleError.value, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            // Mô tả
            RequiredLabel("Mô tả chi tiết")
            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Tối đa 1500 ký tự") },
                isError = viewModel.descriptionError.value.isNotBlank()
            )
            if (viewModel.descriptionError.value.isNotBlank()) {
                Text(viewModel.descriptionError.value, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            // Giá
            RequiredLabel("Giá sản phẩm")
            OutlinedTextField(
                value = viewModel.priceText.value,
                onValueChange = { viewModel.onPriceChange(it) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text("Tối đa 100 triệu") },
                isError = viewModel.priceError.value.isNotBlank()
            )
            if (viewModel.priceError.value.isNotBlank()) {
                Text(viewModel.priceError.value, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            // Địa chỉ
            RequiredLabel("Địa chỉ")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showAddressPicker = true }
            ) {
                OutlinedTextField(
                    value = viewModel.addressName.value.ifBlank { "Chọn địa chỉ" },
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (viewModel.addressName.value.isBlank()) Color.Gray else LocalContentColor.current,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            if (viewModel.addressError.value.isNotBlank()) {
                Text(viewModel.addressError.value, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddressPicker) {
        AddressPickerPopup(
            onDismiss = { showAddressPicker = false },
            onAddressSelected = { province, district, ward ->
                viewModel.setAddress("$ward, $district, $province")
                showAddressPicker = false
            },
            viewModel = hiltViewModel()
        )
    }

    if (showCategoryPicker) {
        CategoryPickerPopup(
            onDismiss = { showCategoryPicker = false },
            onCategorySelected = { category ->
                viewModel.setCategory(category?.name?:"")
                showCategoryPicker = false
            },
            viewModel = hiltViewModel(),
            allowAllRoot = true
        )
    }
}
