package com.example.resell.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.model.Category
import com.example.resell.model.CategoryNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerBottomSheet(
    categoryTree: List<CategoryNode>,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState  = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 4.dp
    ) {
        val expandedNodes = remember { mutableStateMapOf<String, Boolean>() }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Chọn danh mục",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Divider()

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(categoryTree) { node ->
                    CategoryItem(
                        node = node,
                        expandedNodes = expandedNodes,
                        onCategorySelected = {
                            onCategorySelected(it)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    node: CategoryNode,
    expandedNodes: MutableMap<String, Boolean>,
    onCategorySelected: (Category) -> Unit,
    level: Int = 0
) {
    val isExpanded = expandedNodes[node.category.id] ?: false

    Column(modifier = Modifier.fillMaxWidth()) {
        // Node chính có thể mở rộng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (node.children.isNotEmpty()) {
                        expandedNodes[node.category.id] = !isExpanded
                    } else {
                        onCategorySelected(node.category)
                    }
                }
                .padding(start = (16 + level * 12).dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (node.children.isNotEmpty()) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(32.dp))
            }

            Text(
                text = node.category.name,
                fontSize = 16.sp,
                fontWeight = if (node.children.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isExpanded && node.children.isNotEmpty()) {
            // Thêm dòng "Tất cả + tên cha"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(node.category) }
                    .padding(start = (16 + (level + 1) * 12).dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "Tất cả ${node.category.name}",
                    fontSize = 16.sp,
                    fontWeight =  FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Hiển thị các node con
            node.children.forEach { child ->
                CategoryItem(
                    node = child,
                    expandedNodes = expandedNodes,
                    onCategorySelected = onCategorySelected,
                    level = level + 1
                )
            }
        }
    }
}
