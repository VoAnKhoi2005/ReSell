package com.example.resell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.Ward
import com.example.resell.ui.viewmodel.components.AddressPickerViewModel

@Composable
fun AddressPickerPopup(
    viewModel: AddressPickerViewModel,
    onDismiss: () -> Unit,
    onAddressSelected: (Province?, District?, Ward?) -> Unit,
    allowAll: Boolean = true
) {
    val selectedProvince = viewModel.selectedProvince
    val selectedDistrict = viewModel.selectedDistrict
    val selectedWard = viewModel.selectedWard
    val selectionStep = viewModel.selectionStep

    val titleText = when (selectionStep) {
        AddressSelectionStep.Province -> "Chọn tỉnh/thành phố"
        AddressSelectionStep.District -> "Chọn quận/huyện"
        AddressSelectionStep.Ward -> "Chọn phường/xã"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomCenter)
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TopBar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                if (viewModel.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(Modifier.weight(1f)) {
                        // Nút quay lại
                        if (selectionStep != AddressSelectionStep.Province) {
                            Text(
                                text = "← Quay lại",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { viewModel.onBackStep() }
                                    .padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                            )
                        }

                        LazyColumn {
                            // Tùy chọn "Tất cả"
                            if (!allowAll) {
                                item {
                                    val label = when (selectionStep) {
                                        AddressSelectionStep.Province -> "Toàn quốc"
                                        AddressSelectionStep.District -> "Tất cả huyện"
                                        AddressSelectionStep.Ward -> "Tất cả xã"
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                when (selectionStep) {
                                                    AddressSelectionStep.Province -> {
                                                        onAddressSelected(null, null, null)
                                                    }

                                                    AddressSelectionStep.District -> {
                                                        onAddressSelected(selectedProvince, null, null)
                                                    }

                                                    AddressSelectionStep.Ward -> {
                                                        onAddressSelected(selectedProvince, selectedDistrict, null)
                                                    }
                                                }
                                                onDismiss()
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(text = label)
                                        Divider()
                                    }
                                }
                            }

                            // Danh sách tương ứng
                            val items = when (selectionStep) {
                                AddressSelectionStep.Province -> viewModel.provinces
                                AddressSelectionStep.District -> viewModel.districts
                                AddressSelectionStep.Ward -> viewModel.wards
                            }

                            items(items) {
                                val name = when (it) {
                                    is Province -> it.name
                                    is District -> it.name
                                    is Ward -> it.name
                                    else -> ""
                                }

                                val isSelected = when (it) {
                                    is Province -> it == selectedProvince
                                    is District -> it == selectedDistrict
                                    is Ward -> it == selectedWard
                                    else -> false
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            when (it) {
                                                is Province -> viewModel.onProvinceSelected(it)
                                                is District -> viewModel.onDistrictSelected(it)
                                                is Ward -> {
                                                    viewModel.onWardSelected(it)
                                                    onAddressSelected(selectedProvince, selectedDistrict, it)
                                                    onDismiss()
                                                }
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = name,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                                    )
                                    Divider()
                                }
                            }
                        }
                    }

                    // Nút Xong (nếu đã chọn đủ)
                    if (selectedProvince != null && selectedDistrict != null && selectedWard != null) {
                        Button(
                            onClick = {
                                onAddressSelected(selectedProvince, selectedDistrict, selectedWard)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Xong")
                        }
                    }
                }
            }
        }
    }
}

enum class AddressSelectionStep {
    Province, District, Ward
}
