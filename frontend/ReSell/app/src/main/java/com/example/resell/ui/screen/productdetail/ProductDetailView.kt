package com.example.resell.ui.screen.productdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.resell.R
import com.example.resell.ui.components.CircleIconButton
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProfileSimpleHeader
import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.PhoneBox
import com.example.resell.ui.theme.White2
import com.example.resell.ui.theme.priceColor

//test
val sampleImages = listOf(
    "https://picsum.photos/id/1003/800/600",
    "https://i.pinimg.com/736x/61/f7/ee/61f7ee1641e001a99b484c4f84d07e16.jpg",
    "https://picsum.photos/id/1018/800/600"
)
val sampleTitle = "iPhone 14 Pro Max 128GB - Hàng chính hãng"
val samplePrice = 27990000
val sampleCategory = "Điện thoại di động"
val sampleTime = "Cách đây 2 giờ"
val sampleAddress = "Quận 1, TP. Hồ Chí Minh"
val sampleDescription = """
    - Máy mới 100% nguyên seal.
    - Bảo hành chính hãng Apple 12 tháng.
    - Màn hình Super Retina XDR 6.7 inch, Dynamic Island.
    - Camera 48MP chụp ảnh siêu nét, quay phim 4K.
    - Tặng kèm ốp lưng và dán màn hình.
""".trimIndent()

val sampleAvatar = "https://i.pinimg.com/736x/de/5b/8d/de5b8da13f9a4e5d462499d70ef3114d.jpg"
val sampleName = "Nguyễn Văn A"
val sampleRating = "⭐ 4.8 (120 đánh giá)"
val sampleUserId = "user12345"
val sampleFollowerCount = 340
val sampleFollowingCount = 120
//
@Composable
fun ProductDetailScreen(
    images: List<String>,
    title: String,
    price: Int,
    category: String,
    time: String,
    address: String,
    description: String,
    avatarUrl: String?,
    sellerName: String?,
    sellerRating: String?,
    sellerId: String?,
    followerCount: Int?,
    followingCount: Int?,
    onContactClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // ✅ TopBar thêm vào đầu màn
        TopBar(
            titleText = "Chi tiết sản phẩm",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            item {
                ImageCarousel(images = images)
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ProductBasicInfo(title, category, price, time, address)
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    ProfileSimpleHeader(
                        avatarUrl = avatarUrl,
                        name = sellerName,
                        rating = sellerRating,
                        soldCount = 150
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ProductDescription(description)
            }
            item {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Spacer(modifier = Modifier.height(2.dp))
                ActionButtons(
                    onContactClick = onContactClick,
                    onBuyClick = onBuyClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ProductBasicInfo(title: String, category: String, price: Int, time: String, address: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = category, style = MaterialTheme.typography.bodySmall.copy(color = GrayFont))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${price}₫", style = MaterialTheme.typography.titleMedium.copy(color = priceColor))
        Spacer(modifier = Modifier.height(8.dp))
        TimeInfor(time = time, address = address)
    }
}

@Composable
fun ProductDescription(description: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Mô tả chi tiết", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ActionButtons(onContactClick: () -> Unit, onBuyClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonHorizontal(
            text = "Gọi điện",
            textColor = White2,
            iconResId = R.drawable.baseline_phone_24,
            hasBorder = true,
            backgroundColor = PhoneBox,
            contentAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // ✅ chia đều
        ) {

        }
        Spacer(modifier = Modifier.width(12.dp))

        IconButtonHorizontal(
            text = "Chat",
            hasBorder = true,
            iconResId = R.drawable.chat_duotone,
            contentAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // ✅ chia đều
        ) {

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    var isFavorite by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Product image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Icons overlay on top right
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                CircleIconButton(
                    iconResId = if (isFavorite) R.drawable.like else R.drawable.unlike,
                    contentDescription = "Favorite",
                    iconTint = Color.Red
                ) { isFavorite = !isFavorite }

                Spacer(modifier = Modifier.width(8.dp))

                CircleIconButton(
                    iconResId = R.drawable.baseline_report_problem_24,
                    contentDescription = "Report"
                ) { /* TODO: Report */ }

            }
        }

        // Dots indicator(dấu chấm thể hiện đang ở ảnh nào)
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            repeat(images.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 10.dp else 6.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.Black else LightGray)
                )
            }
        }
    }
}
