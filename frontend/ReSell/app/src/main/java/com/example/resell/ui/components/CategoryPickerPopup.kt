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
import com.example.resell.model.Category
import com.example.resell.model.CategoryNode
import com.example.resell.ui.viewmodel.components.CategoryPickerViewModel


@Composable
fun CategoryPickerPopup(
    viewModel: CategoryPickerViewModel,
    onDismiss: () -> Unit,
    onCategorySelected: (Category?) -> Unit,
    allowAllRoot: Boolean = false
)
 {
    val isLoading = viewModel.isLoading.value
    val title = viewModel.getCurrentTitle()
    val nodes = viewModel.currentLevelNodes.value
    val atRoot = viewModel.path.isEmpty()

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
            Column(Modifier.fillMaxSize()) {
                // TopBar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.width(48.dp))
                    }
                }

                if (!atRoot) {
                    Text(
                        "← Quay lại",
                        modifier = Modifier
                            .clickable { viewModel.onBack() }
                            .padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
                        if (atRoot && allowAllRoot) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCategorySelected(null)
                                            onDismiss()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text("Tất cả Danh mục", fontWeight = FontWeight.Medium)
                                    Divider()
                                }
                            }
                        }
                        // Lựa chọn "Tất cả" nếu không phải node gốc
                        if (!atRoot) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCategorySelected(viewModel.getCurrentSelected())
                                            onDismiss()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text("Tất cả ${title.lowercase()}")
                                    Divider()
                                }
                            }
                        }

                        // Danh sách danh mục con
                        items(nodes) { node ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onCategorySelected(node)
                                        if (node.children.isEmpty()) {
                                            onCategorySelected(node.category)
                                            onDismiss()
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(text = node.category.name)
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}
