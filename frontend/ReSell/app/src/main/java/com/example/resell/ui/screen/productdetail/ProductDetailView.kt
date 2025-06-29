package com.example.resell.ui.screen.productdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.components.CircleIconButton
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.IconWithTextRow
import com.example.resell.ui.components.ProductPostItem
import com.example.resell.ui.components.ProfileSimpleHeader
import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.home.ProductPost
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.PhoneBox
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White2
import com.example.resell.ui.theme.priceColor
import com.example.resell.ui.viewmodel.chat.ChatViewModel
import com.example.resell.ui.viewmodel.productDetail.ProductDetailViewModel
import com.example.resell.util.getRelativeTime




@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val post = viewModel.postDetail
    val isLoading by viewModel.isLoading.collectAsState()
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
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
                    ImageCarousel(images = post?.images?.map { it.url }.orEmpty())
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    ProductBasicInfo(
                        title = post?.title ?: "",
                        price = post?.price ?: 0,
                        category = post?.category?.name ?: "",
                        time = getRelativeTime(post?.createdAt),
                        address = post?.address?.detail ?: ""
                    )
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
                            avatarUrl = viewModel.postDetail?.user?.avatarURL
                                ?: stringResource(R.string.default_avatar_url),
                            name = post?.user?.fullName,
                            rating = (viewModel.statSeller?.averageRating as? Number)?.toFloat() ?: 5f,
                            reviewCount = viewModel.statSeller?.reviewNumber,
                            soldCount = viewModel.statSeller?.saleNumber
                        )

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    ProductDescription(post?.description ?: "")
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    ActionButtons(
                        onContactClick = {
                            val phoneNumer = post?.user?.phone ?: ""
                            if (phoneNumer.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${phoneNumer}")
                                }
                                context.startActivity(intent)
                            }
                        },
                        onChatClick = {
                            viewModel.openConversation()
                        },viewModel
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    HorizontalPostListSection(
                        title = "Tin đăng tương tự",
                        posts = viewModel.relatedPosts
                    ) { selectedPost ->
                        NavigationController.navController.navigate(Screen.ProductDetail.route + "/${selectedPost.id}")
                    }
                }

                if (!viewModel.sellerPosts.isEmpty())item {
                    HorizontalPostListSection(
                        title = "Tin đăng của ${post?.user?.username ?: "người bán"}",
                        posts = viewModel.sellerPosts
                    ) { selectedPost ->
                        NavigationController.navController.navigate(Screen.ProductDetail.route + "/${selectedPost.id}")
                    }
                }

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
        IconWithTextRow(
            iconResId = R.drawable.time,
            text = time
        )
        IconWithTextRow(
            iconResId = R.drawable.pin_duotone,
            text = address
        )
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
fun ActionButtons(onContactClick: () -> Unit, onChatClick: () -> Unit,viewModel: ProductDetailViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
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
            modifier = Modifier.weight(1f),
            iconTint = White// ✅ chia đều
        ) {
            onContactClick()
        }
        Spacer(modifier = Modifier.width(12.dp))

        IconButtonHorizontal(
            text = "Chat",
            hasBorder = true,
            iconResId = R.drawable.chat_duotone,
            contentAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f), // ✅ chia đều
        ) {
           if (!isLoading) onChatClick()
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


@Composable
fun HorizontalPostListSection(
    title: String,
    posts: List<PostData>,
    onPostClick: (PostData) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 20.sp),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                ProductPostItem(
                    title = post.title,
                    time = getRelativeTime(post.createdAt),
                    imageUrl = post.thumbnail,
                    price = post.price,
                    category = post.category,
                    address = post.address,
                    modifier = Modifier.width(220.dp)
                ) {
                    onPostClick(post)
                }
            }
        }
    }
}

