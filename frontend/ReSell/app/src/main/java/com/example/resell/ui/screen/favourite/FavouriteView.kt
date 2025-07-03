package com.example.resell.ui.screen.favourite

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.Red
import com.example.resell.ui.theme.White
import com.example.resell.ui.viewmodel.favourite.FavoriteViewModel

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val posts by viewModel.favoritePosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        Log.e("FavoriteScreen", "LaunchedEffect gọi fetchFavoritePosts")
        viewModel.fetchFavoritePosts()
    }

    Column {
        TopBar(
            titleText = "Tin đăng đã lưu",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (posts.isEmpty()) {
                // Hiển thị text khi không có bài đăng yêu thích
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có bài đăng yêu thích nào",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = White),
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    NavigationController.navController.navigate("productdetail_screen/${post.id}")
                                }
                        ) {
                            Box {
                                ProductPostItemHorizontalImage(
                                    title = post.title,
                                    time = post.createdAt?.toString() ?: "",
                                    imageUrl = post.thumbnail,
                                    price = post.price,
                                    address = post.address,
                                    showExtraInfo = false
                                )

                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Đã lưu",
                                    tint = Red,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}
