package com.example.resell.ui.screen.favourite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.Red
import com.example.resell.ui.theme.White

@Composable
fun FavoriteScreen() {
    val favoritePosts = listOf(
        Triple("Gấu Bông Cưng", "3 phút trước", "https://i.pinimg.com/736x/1c/63/8f/1c638ff41962012e45d77018a7be935c.jpg"),
        Triple("Plushie", "1 giờ trước", "https://i.pinimg.com/736x/15/35/c7/1535c759a5e617be2794d128e070a0bf.jpg"),
        Triple("Figure", "Hôm qua", "https://i.pinimg.com/736x/d9/61/99/d961991a35c7ebe017156bd38fbcde56.jpg")
    )

    Column {
        TopBar(
            titleText = "Tin đăng đã lưu",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favoritePosts) { (title, time, imageUrl) ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        ProductPostItemHorizontalImage(
                            title = title,
                            time = time,
                            imageUrl = imageUrl,
                            price = 199000,
                            address = "Quận 1, TP.HCM",
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
