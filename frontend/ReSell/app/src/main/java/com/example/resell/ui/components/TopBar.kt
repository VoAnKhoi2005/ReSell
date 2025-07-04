package com.example.resell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.model.Notification
import com.example.resell.model.NotificationType
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.IconColor
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val background = if (!notification.isRead)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else
        MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(background)
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            NotificationTypeBadge(notification.type)
        }

        notification.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        notification.createdAt?.let {
            Text(
                text = it.toString(), // Or format as "2h ago"
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Divider()
}

@Composable
fun NotificationTypeBadge(type: NotificationType) {
    val label = when (type) {
        NotificationType.MESSAGE -> "Tin nhắn"
        NotificationType.ALERT -> "Cảnh báo"
        NotificationType.SYSTEM -> "Hệ thống"
        NotificationType.ORDER -> "Đơn hàng"
        NotificationType.DEFAULT -> "Khác"
    }

    val color = when (type) {
        NotificationType.MESSAGE -> Color(0xFF42A5F5)
        NotificationType.ALERT -> Color(0xFFFF7043)
        NotificationType.SYSTEM -> Color(0xFFAB47BC)
        NotificationType.ORDER -> Color(0xFF66BB6A)
        NotificationType.DEFAULT -> Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showSearch: Boolean = false,
    searchQuery: String = "",
    onClearSearch: () -> Unit = {},
    onSearchNavigate: (() -> Unit)? = null,
    titleText: String? = null,
    showNotificationIcon: Boolean = false,
    showEmailIcon: Boolean = false,
    showBackButton: Boolean = false,
    notifications: List<Notification> = emptyList(),
    onMarkAsRead: (Notification) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    var showDropdown by remember { mutableStateOf(false) }

    Box {
        TopAppBar(
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftBlue),
            title = {
                when {
                    showSearch -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
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
                    IconButton(onClick = { showDropdown = !showDropdown }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(30.dp),
                            tint = IconColor
                        )
                    }
                }

                if (showEmailIcon) {
                    IconButton(onClick = {
                        NavigationController.navController.navigate(Screen.ChatHome.route)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.messages),
                            contentDescription = "Chat",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(30.dp),
                            tint = IconColor
                        )
                    }
                }
            }
        )

        if (showDropdown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(onClick = { showDropdown = false })
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp, end = 16.dp)
                        .width(320.dp)
                        .heightIn(max = 400.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column {
                        Text(
                            "Thông báo",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                        HorizontalDivider()

                        if (notifications.isEmpty()) {
                            Text(
                                "Không có thông báo",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                items(notifications) { notification ->
                                    NotificationItem(
                                        notification = notification,
                                        onClick = {
                                            onMarkAsRead(notification)
                                            showDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
