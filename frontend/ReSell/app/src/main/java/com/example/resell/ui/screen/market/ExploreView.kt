package com.example.resell.ui.screen.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Dùng để quản lý Column, Row, Spacer, Box...
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // ✅ Nếu bạn dùng ảnh qua URL
import coil.request.ImageRequest

import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.UserMessage
import com.example.resell.ui.theme.White2
import com.example.resell.util.getRelativeTime



@Composable
fun ExploreScreen(posts: List<PostData>, onLoadMore: () -> Unit) {


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
            PostItemView(
                avatarUrl =  post.avatar,
                name = post.fullname,
                time = getRelativeTime( post.createdAt),
                address = post.address,
                price = post.price,
                images = post.images?.map { it.url }?:emptyList(),
                title = post.title,
                postId = post.id,
                contentDescription = post.description
            )

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

    }
        item {
            LaunchedEffect(Unit) {
                onLoadMore() // gọi khi cuộn đến cuối
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
    price: Int,
    postId: String,
    contentDescription: String,
    isFollowing: Boolean = true,
    onSaveClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onFollowClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Profile Header (avatar, name, follow) — click toàn bộ để mở profile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /*NavigationController.navController.navigate(Screen.ProductDetail.route+"/${userId}") */}
            ) {
                ProfileHeaderWithFollow(
                    avatarUrl = avatarUrl,
                    name = name,
                    isFollowing = isFollowing,
                    onFollowClick = onFollowClick
                )
            }

            TimeInfor(time = time, address = address)

            Spacer(modifier = Modifier.height(8.dp))

            ImageCarousel(images = images)

            Spacer(modifier = Modifier.height(8.dp))

            // Box chứa title + price + icon >
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { NavigationController.navController.navigate(Screen.ProductDetail.route+"/${postId}") }
                    .background(LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "%,d ₫".format(price),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Chi tiết",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = contentDescription,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = LightGray, thickness = 0.7.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Chat button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
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
}

@Composable
fun ProfileHeaderWithFollow(
    avatarUrl: String?,
    name: String,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with fallback & error
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .error(R.drawable.defaultavatar)
                .fallback(R.drawable.defaultavatar)
                .build(),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onFollowClick,
            modifier = Modifier
                .height(32.dp)
                .defaultMinSize(minWidth = 90.dp),
            shape = RoundedCornerShape(8.dp),
            colors = if (isFollowing)
                ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = Color.Black)
            else
                ButtonDefaults.buttonColors(containerColor = UserMessage, contentColor = White2),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
        ) {
            Text(
                text = if (isFollowing) "Đang theo dõi" else "+ Theo dõi",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp)
            )
        }
    }
}

