package com.example.resell.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.resell.R
import com.example.resell.ui.components.ProductPostItem
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White1
import com.example.resell.ui.viewmodel.home.postList

@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) { HomeContent() }

}

@Composable
fun HomeContent(modifier: Modifier = Modifier) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 cột
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .drawBehind{
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Tin đăng dành cho bạn",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alignByBaseline(),


                )
                Spacer(modifier = Modifier.padding(2.dp))
                Icon(
                    painter = painterResource(id = R.drawable.filter_horizontal),
                    contentDescription = "Gợi ý",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            // 👉 Xử lý khi click vào icon ở đây
                            println("Icon được click")
                        }
                        .padding(bottom = 16.dp),
                    tint = DarkBlue
                )
            }
        }
        items(postList) { post ->
            ProductPostItem(
                title = post.title,
                time = post.time,
                imageUrl = post.imageUrl,
                price = post.price,
                category = post.category,
                address = post.address,
                modifier = Modifier.fillMaxWidth()
            ){
                NavigationController.navController.navigate(Screen.ProductDetail.route)
            }
        }
    }
}
data class ProductPost(//test
    val title: String,
    val time: String,
    val imageUrl: String,
    val price: Int,
    val category: String,
    val address: String
)

