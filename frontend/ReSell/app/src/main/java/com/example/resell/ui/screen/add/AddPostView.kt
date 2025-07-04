// AddPostScreen.kt
package com.example.resell.ui.screen.add

import android.net.Uri
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.resell.ui.components.AddressPickerPopup
import com.example.resell.ui.components.CategoryPickerPopup
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.screen.home.HomeContent
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.addpost.AddPostViewModel
import com.example.resell.ui.viewmodel.components.AddressPickerViewModel
import com.example.resell.ui.viewmodel.components.CategoryPickerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(
    modifier: Modifier = Modifier,
    viewModel: AddPostViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val addressPickerViewModel: AddressPickerViewModel = hiltViewModel()
    val categoryPickerViewModel: CategoryPickerViewModel = hiltViewModel()
    var showAddressPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val availableSlots = 6 - viewModel.imageUrls.size
        val limitedUris = uris.take(availableSlots)
        viewModel.addImages(limitedUris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng Tin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick =
                        {
                            NavigationController.navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = White2,
                    navigationIconContentColor = White2
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
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.submitPost()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.isReadyToSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isReadyToSubmit) DarkBlue else GrayFont,
                        contentColor = if (viewModel.isReadyToSubmit) White2 else Color.Black
                    )
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
                        Text("Đăng tin")
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
                        value = viewModel.categoryName.ifBlank { "Chọn danh mục" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = if (viewModel.categoryName.isBlank()) Color.Gray else LocalContentColor.current,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                if (viewModel.categoryError.isNotBlank()) {
                    Text(
                        viewModel.categoryError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Ảnh
                RequiredLabel("Chọn ảnh sản phẩm", showAsterisk = false)
                ImagePickerRow(
                    imageUrls = viewModel.imageUrls,
                    onAddClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    onRemove = { index -> viewModel.removeImageAt(index) }
                )

                Spacer(Modifier.height(16.dp))

                // Tiêu đề
                RequiredLabel("Tiêu đề")
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tối đa 50 ký tự") },
                    isError = viewModel.titleError.isNotBlank()
                )
                if (viewModel.titleError.isNotBlank()) {
                    Text(
                        viewModel.titleError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Mô tả
                RequiredLabel("Mô tả chi tiết")
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Tối đa 1500 ký tự") },
                    isError = viewModel.descriptionError.isNotBlank()
                )
                if (viewModel.descriptionError.isNotBlank()) {
                    Text(
                        viewModel.descriptionError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Giá
                RequiredLabel("Giá sản phẩm")
                OutlinedTextField(
                    value = viewModel.priceText,
                    onValueChange = { viewModel.onPriceChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Tối đa 100 triệu") },
                    isError = viewModel.priceError.isNotBlank()
                )
                if (viewModel.priceError.isNotBlank()) {
                    Text(
                        viewModel.priceError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
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
                        value = viewModel.addressName.ifBlank { "Chọn địa chỉ" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = if (viewModel.addressName.isBlank()) Color.Gray else LocalContentColor.current,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                if (viewModel.addressError.isNotBlank()) {
                    Text(
                        viewModel.addressError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

    }

    if (showAddressPicker) {
        AddressPickerPopup(
            viewModel = addressPickerViewModel,
            onDismiss = { showAddressPicker = false },
            onAddressSelected = { province, district, ward ->
                viewModel.setAddress(province, district, ward)
                showAddressPicker = false
            }
        )
    }

    if (showCategoryPicker) {
        CategoryPickerPopup(
            viewModel = categoryPickerViewModel,
            onCategorySelected = {
                viewModel.setCategory(it,categoryPickerViewModel.getFullCategoryPath(it))
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false },
            allowAllRoot = true
        )
    }
}

@Composable
fun RequiredLabel(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    showAsterisk: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        if (showAsterisk) {
            Text(
                text = " *",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ImagePickerRow(
    imageUrls: List<Uri>,
    onAddClick: () -> Unit,
    onRemove: (Int) -> Unit
) {
    LazyRow(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        item {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        items(imageUrls.size) { index ->
            Box(modifier = Modifier.padding(start = 8.dp)) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrls[index]),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onRemove(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
