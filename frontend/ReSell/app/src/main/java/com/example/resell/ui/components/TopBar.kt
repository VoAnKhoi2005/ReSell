package com.example.resell.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.IconColor
import com.example.resell.ui.theme.SoftBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showSearch: Boolean = false,
    titleText: String? = null,
    showNotificationIcon: Boolean = false,
    showEmailIcon: Boolean = false,
    onSearchNavigate: (() -> Unit)? = null
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
                            onSearchNavigate?.invoke() // Gọi callback để điều hướng
                        }
                    )
                }
                titleText != null -> {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }
        },
        actions = {
            if (showNotificationIcon) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp).size(30.dp),
                    tint = IconColor,

                )
            }

            if (showEmailIcon) {

                IconButton(onClick = {
                    NavigationController.navController.navigate(Screen.ChatHome.route)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.messageicon),
                        contentDescription = "Chat",
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    )
}
