package com.example.resell.ui.screen.rating

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.StarRating
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.White

@Composable
fun RatingFromBuyerScreen() {
    val ratings = listOf(
        RatingItemData(
            userName = "Chúa MHề 4.0",
            userAvatar = "https://i.pinimg.com/736x/b0/70/7e/b0707e4393b10460a9b54eeb9a567af9.jpg",
            rating = 4.5f,
            productTitle = "Áo hoodie local brand",
            productImage = "https://bizweb.dktcdn.net/100/415/697/products/hdfpolix-1-2n62-hinh-mat-truoc-0.jpg?v=1703061104020"
        ),
        RatingItemData(
            userName = "Minh Bé Khỏe",
            userAvatar = "https://i.pinimg.com/736x/bd/22/86/bd2286807e2df87e35a2f8cd26235ba9.jpg",
            rating = 3.8f,
            productTitle = "Giày Nike cổ thấp",
            productImage = "https://bizweb.dktcdn.net/100/467/909/products/giay-nike-air-force-1-low-colleg-6.jpg?v=1722600447913"
        ),
        RatingItemData("Huy Năng", "https://i.pinimg.com/736x/49/46/64/4946640e74c97b1e8b42029bc8d40380.jpg", 5.0f, "Túi local", "https://bizweb.dktcdn.net/100/287/440/products/tui-deo-cheo-local-brand-dep-nam-nu-vai-da-mau-den-1.jpg?v=1698643859327"),
        RatingItemData("Quang Bảo", "https://i.pinimg.com/736x/ee/62/6a/ee626ad0dcf0a1d82d3ebbfcf97043fd.jpg", 2.5f, "Áo oversize", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSRclDycxvuNMPdxI3Z5oyWb58DtyWTdwVF9A&s")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ratings) { rating ->
            RatingItem(data = rating)
            Divider()
        }
    }
}


// 🔸 Dữ liệu 1 đánh giá
data class RatingItemData(
    val userName: String,
    val userAvatar: String,
    val rating: Float,
    val productTitle: String,
    val productImage: String
)

// 🔸 Giao diện 1 đánh giá
@Composable
fun RatingItem(data: RatingItemData, currentUserName: String = "Chúa MHề 4.0") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            //  Avatar + Tên người đánh giá + Nút Sửa nếu đúng user
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(data.userAvatar),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = data.userName,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Nút "Sửa" nếu là người đánh giá
                if (data.userName == currentUserName) {
                    Text(
                        text = "Sửa",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable {
                            NavigationController.navController.navigate(Screen.ReviewProductScreen.route)
                        }
                    )
                }
            }

            // ⭐ Star rating
            StarRating(
                rating = data.rating,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 📦 Sản phẩm được đánh giá
            ProductPostItemHorizontalImage(
                title = data.productTitle,
                time = "2 ngày trước",
                imageUrl = data.productImage,
                price = 199000,
                address = "Quận 1, TP.HCM",
                showExtraInfo = false
            )
        }
    }
}

