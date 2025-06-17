package com.example.resell.ui.screen.market

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProfileSimpleHeader

import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.LightGray

data class PostData(
    val avatar: String?,
    val name: String,
    val time: String,
    val address: String,
    val images: List<String>,
    val title: String,
    val content: String
)

@Composable
fun ExploreScreen() {
    val samplePosts = listOf(
        PostData(
            name = "Nguyễn Văn A",
            avatar = "https://i.pinimg.com/736x/39/91/f1/3991f1840e975b01f9b205d97f644d98.jpg",
            time = "Cách đây 2 giờ",
            address = "Quận 1, TP. Hồ Chí Minh",
            images = listOf(
                "https://picsum.photos/id/1003/800/600",
                "https://picsum.photos/id/1011/800/600"
            ),
            title = "iPhone 14 Pro Max 128GB - Hàng chính hãng",
            content = "Máy mới 100%, bảo hành 12 tháng chính hãng. Có hỗ trợ trả góp lãi suất 0%."
        ),
        PostData(
            name = "Trần Thị B",
            avatar = "https://i.pinimg.com/736x/39/83/63/398363cdc6c47752bccb799b8a666935.jpg",
            time = "Cách đây 30 phút",
            address = "Thủ Đức, TP.HCM",
            images = listOf(
                "https://picsum.photos/id/1024/800/600"
            ),
            title = "MacBook Air M2 2023",
            content = "Máy mỏng nhẹ, pin trâu. Thích hợp cho học sinh, sinh viên, văn phòng."
        ),
        PostData(
            name = "Lê Văn C",
            avatar = "https://i.pinimg.com/736x/da/7e/4c/da7e4cd7e1f8fd4758f0e15f6ea13932.jpg",
            time = "1 ngày trước",
            address = "Bình Thạnh, TP.HCM",
            images = listOf(
                "https://picsum.photos/id/1035/800/600",
                "https://picsum.photos/id/1033/800/600",
                "https://picsum.photos/id/1039/800/600"
            ),
            title = "Xe máy Honda Vision 2022",
            content = "Xe đi 8.000km, bảo dưỡng đầy đủ, giấy tờ hợp lệ. Bao sang tên."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(samplePosts) { index, post ->
            PostItemView(
                avatarUrl = post.avatar,
                name = post.name,
                time = post.time,
                address = post.address,
                images = post.images,
                title = post.title,
                contentDescription = post.content
            )

            if (index < samplePosts.lastIndex) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                       ,
                    color = LightGray,
                    thickness = 4.dp
                )
            }
        }
    }

}

@Composable
fun PostItemView(
    avatarUrl: String?,
    name: String,
    time: String,
    address: String,
    images: List<String>,
    title: String,
    contentDescription: String,
    onSaveClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp)
    ) {
        // Avatar + Name
        ProfileSimpleHeader(
            avatarUrl = avatarUrl,
            name = name,
            rating = null,
            soldCount = null
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Time + Address
        TimeInfor(time = time, address = address)

        Spacer(modifier = Modifier.height(8.dp))

        // Image Carousel
        ImageCarousel(images = images)

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Content Description (giới hạn 3 dòng)
        Text(
            text = contentDescription,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = LightGray, thickness = 1.dp)


        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButtonHorizontal(
                text = "Lưu tin",
                iconResId = R.drawable.favorite,
                hasBorder = false,
                contentAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                onClick = onSaveClick
            )

            IconButtonHorizontal(
                text = "Chat",
                iconResId = R.drawable.chat_duotone,
                hasBorder = false,
                contentAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                onClick = onChatClick
            )
        }
    }
}


