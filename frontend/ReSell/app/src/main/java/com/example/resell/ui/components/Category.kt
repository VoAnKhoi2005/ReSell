package com.example.resell.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.draw.clip
import com.example.resell.R
import com.example.resell.ui.theme.LoginButton

//test
data class CategoryItemData(
    val title: String,
    val iconResId: Int
)
@Composable
fun CategoryItemButton(
    item: CategoryItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(88.dp)
            .height(112.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Ảnh (icon)
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.iconResId),
                contentDescription = item.title,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Text căn giữa theo chiều ngang của Column
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 2,
            softWrap = true,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


@Composable//hàm để có 2 dòng danh mục
fun Horizontal2RowCategoryGrid(
    categoryList: List<CategoryItemData>,
    onItemClick: (CategoryItemData) -> Unit
) {
    val grouped = categoryList.chunked(2) // mỗi 2 item làm 1 cột (1 item của LazyRow)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(grouped) { columnItems ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                columnItems.forEach { item ->
                    CategoryItemButton(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable//bottomsheet danh mục
fun CategoryFilterBottomSheet(
    onDismissRequest: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "Xe cộ" to R.drawable.car_category,
        "Đồ điện tử" to R.drawable.electronic_category,
        "Đồ nội thất gia dụng, cây cảnh" to R.drawable.furniture_category,
        "Dịch vụ, du lịch" to R.drawable.service_category,
        "Thú cưng" to R.drawable.pet_category,
        "Tủ lạnh, máy lạnh, máy giặt" to R.drawable.fridge_category,
        "Đồ dùng văn phòng" to R.drawable.offices_category,
        "Thời trang, đồ cá nhân" to R.drawable.clothes_category,
        "Đồ ăn thực phẩm" to R.drawable.food_category,
        "Mẹ và bé" to R.drawable.baby_category,
        "Cho tặng miễn phí" to R.drawable.giveaway_category
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Chọn danh mục",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn {
                items(categories) { (title, iconRes) ->
                    IconButtonHorizontal(
                        text = title,
                        iconResId = iconRes,
                        hasBorder = true,
                        contentAlignment = Alignment.Start,
                        textColor = LoginButton,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        onCategorySelected(title)
                        onDismissRequest()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
