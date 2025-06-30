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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.snapshotFlow
import com.example.resell.ui.components.Horizontal2RowCategoryGrid
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val postList by viewModel.postList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val gridState = rememberLazyGridState()
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
            HomeContent(postList,viewModel=viewModel, gridState = gridState)
        }
    }
}

@Composable
fun HomeContent(postList: List<ProductPost>, modifier: Modifier = Modifier,viewModel: HomeViewModel, gridState: LazyGridState,) {
val categoryList by viewModel.categoryList.collectAsState()

    // 👇 Lắng nghe khi user scroll tới gần cuối (VD: còn 2 item)
    LaunchedEffect(Unit) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                val total = layoutInfo.totalItemsCount
                val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisible to total
            }
            .distinctUntilChanged()
            .filter { (lastVisible, total) ->
                // ✅ Chỉ gọi loadMore nếu scroll được, và đang ở gần cuối
                val canScroll = gridState.canScrollForward
                lastVisible >= total - 4 && total > 0 && canScroll && !viewModel.isLoading.value
            }
            .collect {
                viewModel.loadMore()
            }
    }


    LazyVerticalGrid(
        state = gridState,
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
                        onItemClick = { NavigationController.navController.navigate(Screen.ResultSearchScreen.route+"//${it.id}") }
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

