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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.model.Follow
import com.example.resell.model.PostData
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProfileSimpleHeader

import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.FollowButton
import com.example.resell.ui.theme.UserMessage
import com.example.resell.ui.theme.White2
import com.example.resell.util.getRelativeTime

@Composable
fun FollowScreen(posts: List<PostData>, onLoadMore: () -> Unit) {

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
                            .fillMaxWidth(),
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