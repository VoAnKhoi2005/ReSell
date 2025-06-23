package com.example.resell.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.resell.R
import com.example.resell.ui.components.ProductPostItem
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White1
import com.example.resell.ui.viewmodel.home.HomeViewModel
import androidx.compose.runtime.getValue
import com.example.resell.ui.components.CategoryItemButton
import com.example.resell.ui.components.CategoryItemData
import com.example.resell.ui.components.Horizontal2RowCategoryGrid

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val postList by viewModel.postList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            HomeContent(postList)
        }
    }
}

@Composable
fun HomeContent(postList: List<ProductPost>, modifier: Modifier = Modifier) {
    val categoryList = listOf(
        CategoryItemData("Xe cộ", R.drawable.car),
        CategoryItemData("Đồ điện tử", R.drawable.electronic),
        CategoryItemData("Đồ nội thất gia dụng", R.drawable.furniture),
        CategoryItemData("Mẹ và bé", R.drawable.baby),
        CategoryItemData("Dịch vụ, du lịch", R.drawable.travel),
        CategoryItemData("Thú cưng", R.drawable.pet),
        CategoryItemData("Tủ lạnh, máy lạnh, máy giặt", R.drawable.fridge),
        CategoryItemData("Đồ dùng văn phòng", R.drawable.offices),
        CategoryItemData("Thời trang, đồ cá nhân", R.drawable.clothes),
        CategoryItemData("Đồ ăn thực phẩm", R.drawable.food),
        CategoryItemData("Cho tặng miễn phí", R.drawable.giveaway)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                // Khám phá danh mục
                Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 2.dp)) {
                    Text(
                        text = "Khám phá danh mục",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Horizontal2RowCategoryGrid(
                        categoryList = categoryList,
                        onItemClick = { println("Chọn danh mục: ${it.title}") }
                    )


                }
                // Tiêu đề "Tin đăng dành cho bạn"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .drawBehind {
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
                            .alignByBaseline()
                    )

                    Spacer(modifier = Modifier.padding(2.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.filter_horizontal),
                        contentDescription = "Gợi ý",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { println("Icon được click") }
                            .padding(bottom = 16.dp),
                        tint = DarkBlue
                    )
                }
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
            ) {
                NavigationController.navController.navigate(Screen.ProductDetail.route+"/${post.id}")
            }
        }
    }
}
data class ProductPost(//test
    val id:String,
    val title: String,
    val time: String,
    val imageUrl: String,
    val price: Int,
    val category: String,
    val address: String
)

