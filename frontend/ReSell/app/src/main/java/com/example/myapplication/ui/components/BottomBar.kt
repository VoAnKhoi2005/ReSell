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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DarkBlue
import com.example.myapplication.ui.theme.MainButton
import com.example.myapplication.ui.theme.SoftBlue

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String,
    val route: String

)
val bottomNavItems = listOf(
    BottomNavItem(Icons.Default.Home, "Trang chủ", "Trang chủ","home"),
    BottomNavItem(Icons.Default.Create, "Quản lý tin", "Quản lý tin","manage_posts"),
    BottomNavItem(Icons.Default.AddCircle, "Đăng tin", "Đăng tin","create_post"),
    BottomNavItem(Icons.Default.ShoppingCart, "Dạo chợ", "Dạo chợ","market"),
    BottomNavItem(Icons.Default.AccountCircle, "Tài khoản", "Tài khoản","account")
)
@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    selectedItem: BottomNavItem?
) {
    NavigationBar(
        containerColor = SoftBlue
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    IconButtonVertical(
                        icon = item.icon,
                        label = item.label,
                        contentDescription = item.contentDescription,
                        iconTint = if (isSelected)  DarkBlue else MainButton
                    )
                },
                label = { },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkBlue,

                    selectedTextColor = DarkBlue,

                    indicatorColor = Color.White.copy(alpha = 0.3f) // màu nền khi chọn (như highlight) // Đã dùng label trong IconButtonVertical nên để trống
            )
            )
        }
    }
}


