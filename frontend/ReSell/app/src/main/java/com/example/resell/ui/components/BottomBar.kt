package com.example.resell.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.MainButton
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String,
    val screen: Screen // THÊM SCREEN
)

val bottomNavItems = listOf(
    BottomNavItem(Icons.Default.Home, "Trang chủ", "Trang chủ", Screen.Home),
    BottomNavItem(Icons.Default.Create, "Quản lý tin", "Quản lý tin", Screen.Manage),
    BottomNavItem(Icons.Default.Add, "Đăng tin", "Đăng tin", Screen.Add),
    BottomNavItem(Icons.Default.ShoppingCart, "Dạo chợ", "Dạo chợ", Screen.Market),
    BottomNavItem(Icons.Default.AccountCircle, "Tài khoản", "Tài khoản", Screen.Profile)
)

@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    selectedItem: BottomNavItem
) {
    val centerIndex = items.indexOfFirst { it.label == "Đăng tin" }
    val filteredItems = items.toMutableList().apply { removeAt(centerIndex) }

    Box {
        NavigationBar(
            containerColor = White,
            modifier = Modifier
                .drawBehind {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        ) {
            filteredItems.forEachIndexed { index, item ->
                // Đảm bảo thêm chỗ trống giữa
                if (index == 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                val isSelected = item == selectedItem
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemClick(item) },
                    icon = {
                        IconButtonVertical(
                            icon = item.icon,
                            label = item.label,
                            contentDescription = item.contentDescription,
                            iconTint = if (isSelected) DarkBlue else MainButton,
                            labelColor = if (isSelected) DarkBlue else GrayFont
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBlue,
                        selectedTextColor = DarkBlue,
                        unselectedIconColor = MainButton,
                        indicatorColor = Color.Transparent
                    ),
                    alwaysShowLabel = true
                )
            }
        }

        FloatingActionButton(
            onClick = { onItemClick(items[centerIndex]) },
            containerColor = DarkBlue,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .size(60.dp), // làm to hơn
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = items[centerIndex].icon,
                contentDescription = "Đăng tin",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

