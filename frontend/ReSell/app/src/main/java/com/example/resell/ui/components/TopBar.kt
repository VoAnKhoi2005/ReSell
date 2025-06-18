package com.example.resell.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    titleText: String? = null,
    showNotificationIcon: Boolean = false,
    showEmailIcon: Boolean = false,
    onSearchNavigate: (() -> Unit)? = null,
    showBackButton: Boolean = false, // ✅ Cho phép hiển thị nút back
    onBackClick: (() -> Unit)? = null // ✅ Callback cho nút back
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SoftBlue
        ),
        title = {
            when {
                showSearch -> {
                    MySearchBar(
                        modifier = Modifier.padding(bottom = 16.dp),
                        onActivateSearch = {
                            onSearchNavigate?.invoke()
                        }
                    )
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
