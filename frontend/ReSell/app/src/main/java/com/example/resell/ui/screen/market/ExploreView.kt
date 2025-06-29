package com.example.resell.ui.screen.market

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProfileSimpleHeader

import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.FollowButton
import com.example.resell.ui.theme.UserMessage
import com.example.resell.ui.theme.White2



@Composable
fun ExploreScreen(posts: List<PostData>) {


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có bài đăng nào",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
        itemsIndexed(posts) { index, post ->
//            PostItemView(
//                avatarUrl = post.avatar,
//                name = post.name,
//                time = post.time,
//                address = post.address,
//                images = post.images,
//                title = post.title,
//                contentDescription = post.content
//            )

            if (index < posts.lastIndex) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                       ,
                    color = LightGray,
                    thickness = 4.dp
                )
            }
        }
    }    }

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
    onChatClick: () -> Unit = {},
    onFollowClick: () -> Unit={}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp)
    ) {
        // Avatar + Name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ProfileSimpleHeader(
                    avatarUrl = avatarUrl,
                    name = name,
                    rating = null,
                    soldCount = null,
                    reviewCount = null,
                    showRating = false
                )
            }

            Button(
                onClick = onFollowClick,
                modifier = Modifier
                    .height(36.dp)
                    .defaultMinSize(minWidth = 90.dp), // ✅ Đảm bảo nút không bị co
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UserMessage,
                    contentColor = White2
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+ Theo dõi",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                )
            }
        }


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
                text = "Chat",
                iconResId = R.drawable.chat_duotone,
                hasBorder = false,
                contentAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onClick = onChatClick
            )
        }
    }
}
