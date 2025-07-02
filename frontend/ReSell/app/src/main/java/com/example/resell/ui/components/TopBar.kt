package com.example.resell.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.IconColor
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showSearch: Boolean = false,
    searchQuery: String = "",
    onClearSearch: () -> Unit = {},
    onSearchNavigate: (() -> Unit)? = null, // ✅ Thêm dòng này
    titleText: String? = null,
    showNotificationIcon: Boolean = false,
    showEmailIcon: Boolean = false,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftBlue),
        title = {
            when {
                showSearch -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp) // 👈 đẹp mắt
                    ) {
                        MySearchBar(
                            searchQuery = searchQuery,
                            onClearSearch = onClearSearch,
                            onSearchNavigate = onSearchNavigate
                        )
                    }
                }

                titleText != null -> {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = White2
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { onBackClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White2
                    )
                }
            }
        },
        actions = {
            actions()
            if (showNotificationIcon) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .size(30.dp),
                    tint = IconColor
                )
            }

            if (showEmailIcon) {
                IconButton(onClick = {
                    NavigationController.navController.navigate(Screen.ChatHome.route)
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.messages),
                        contentDescription = "Chat",
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .size(30.dp),
                        tint = IconColor
                    )
                }
            }
        }
    )
}
