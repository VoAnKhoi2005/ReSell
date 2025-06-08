package com.example.myapplication.ui.components

import android.hardware.lights.Light
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
)
val bottomNavItems = listOf(
    BottomNavItem(Icons.Default.Home, "Trang chủ", "Trang chủ"),
    BottomNavItem(Icons.Default.Create, "Quản lý tin", "Quản lý tin"),
    BottomNavItem(Icons.Default.ShoppingCart, "Dạo chợ", "Dạo chợ"),
    BottomNavItem(Icons.Default.AccountCircle, "Tài khoản", "Tài khoản"),
    BottomNavItem(Icons.Default.AddCircle, "Đăng tin", "Đăng tin")
)
@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    selectedItem: BottomNavItem?
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemClick(item) },
                icon = {
                    IconButtonVertical(
                        icon = item.icon,
                        label = item.label,
                        contentDescription = item.contentDescription
                    )
                },
                label = { } // Đã dùng label trong IconButtonVertical nên để trống
            )
        }
    }
}


