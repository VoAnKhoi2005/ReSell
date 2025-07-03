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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ReportPostPopup(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    val reasons = listOf(
        "Lừa đảo",
        "Trùng lặp",
        "Hàng đã bán",
        "Không liên lạc được",
        "Thông tin không đúng thực tế",
        "Hàng giả, Hàng nhái, Hàng dựng",
        "Hàng hư hỏng sau khi mua",
        "Lý do khác"
    )

    var selectedReason by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf("") }

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
                            text = "Báo cáo bài đăng",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = "Chọn lý do:",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(reasons) { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 16.dp, vertical = 4.dp) // 👈 Giảm vertical padding
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = reason)
                        }
                    }

                    item {
                        Text(
                            text = "Chi tiết lý do:",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = detail,
                            onValueChange = {
                                if (it.length <= 500) detail = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .heightIn(min = 120.dp), // 👈 Luôn cao ít nhất 5 dòng
                            maxLines = 5,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            placeholder = { Text("Nhập chi tiết lý do...") },
                            supportingText = {
                                Text(
                                    "${detail.length}/500",
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Button(
                    onClick = {
                        if (selectedReason != null) {
                            onSubmit(selectedReason!!, detail.trim())
                            onDismiss()
                        }
                    },
                    enabled = selectedReason != null && detail.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Gửi báo cáo")
                }
            }
        }
    }
}
