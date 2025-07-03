package com.example.resell.ui.screen.market
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.ProfileSimpleHeader
import com.example.resell.ui.components.ReportPostPopup
import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.postmanagement.PostList
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.UserMessage
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.components.ReportPostViewModel
import com.example.resell.ui.viewmodel.market.MarketViewModel
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import com.example.resell.util.getRelativeTime
import kotlinx.coroutines.launch

@Composable
fun MarketScreen() {
    val viewModel : MarketViewModel = hiltViewModel()
    val reportPopupViewModel: ReportPostViewModel = hiltViewModel()
    val listFollow by viewModel.followPosts.collectAsState()
    val listExplore by viewModel.explorePosts.collectAsState()
    val listRecommended by viewModel.recommendedPosts.collectAsState()
    val pagerState = rememberPagerState(pageCount = { MarketTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    val scope = rememberCoroutineScope()

    var showReportPopup by remember { mutableStateOf(false) }
    var reportPostId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            containerColor = Color.White,
            contentColor = DarkBlue
        ){
            MarketTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    text = {
                        Text(tab.text, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    }
                )
            }
        }

        // Nội dung theo tab
        HorizontalPager(state = pagerState, modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) { pageIndex ->
            when (MarketTab.entries[pageIndex]) {
                MarketTab.Follow -> MarketTabScreen(
                    listFollow,
                    marketViewModel =viewModel,
                    onLoadMore = {viewModel.getMoreFollow()},
                    onReportClick = { postId ->
                        reportPostId = postId
                        showReportPopup = true
                    })
                MarketTab.Explore -> MarketTabScreen(
                    listExplore,
                    marketViewModel  =viewModel,
                    onLoadMore = {viewModel.getMoreExplore()},
                    onReportClick = { postId ->
                        reportPostId = postId
                        showReportPopup = true
                    })
                MarketTab.Recommended -> MarketTabScreen(
                    listRecommended,
                    marketViewModel = viewModel,
                    onLoadMore = {viewModel.getMoreRecommended()},
                    onReportClick = { postId ->
                        reportPostId = postId
                        showReportPopup = true
                    })
            }
        }

    }
    if (showReportPopup && reportPostId != null) {
        ReportPostPopup(
            onDismiss = { showReportPopup = false },
            onSubmit = { reason, detail ->
                reportPopupViewModel.report(reportPostId!!, reason, detail)
                if (reportPopupViewModel.reportResult.value==true)
                    showReportPopup = false

            }
        )
    }
}
@Composable
fun MarketTabScreen(posts: List<PostData>,
                    marketViewModel: MarketViewModel,
                    onLoadMore:() ->Unit,
                    onReportClick: (String) -> Unit){


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
                    postData = post,
                    onChatClick = {
                        marketViewModel.openConversation(post.id)
                    },
                    onFollowClick = { marketViewModel.toggleFollow(post.userId, post.isFollowing) },
                    onSaveClick =
                        {
                            marketViewModel.toggleFavorite(postId = post.id,post.isFavorite)
                        },
                    onReportClick = onReportClick
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
                onLoadMore
            }
        }
    }

}


@Composable
fun PostItemView(
    postData: PostData,
    onSaveClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onReportClick: (String) -> Unit
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
                    .clickable { /*NavigationController.navController.navigate(Screen.ProductDetail.route+"/${userId}") */ }
            ) {
                ProfileHeaderWithFollow(
                    avatarUrl = postData.avatar,
                    name = postData.fullname,
                    isFollowing = postData.isFollowing,
                    onFollowClick = onFollowClick
                )
            }

            TimeInfor(time = getRelativeTime(postData.createdAt), address = postData.address)

            Spacer(modifier = Modifier.height(8.dp))

            ImageCarousel(images =postData.images?.map { it.url }?:emptyList(),
                isFavorite = postData.isFavorite,
                onFavoriteClick = onSaveClick,
                onReportClick ={
                    onReportClick(postData.id)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Box chứa title + price + icon >
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { NavigationController.navController.navigate(Screen.ProductDetail.route + "/${postData.id}") }
                    .background(LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = postData.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "%,d ₫".format(postData.price),
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
                text = postData.description,
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


enum class MarketTab(
    val text: String
){
    Follow(
        text ="ĐANG THEO DÕI"
    ),

    Explore(
        text ="KHÁM PHÁ"
    ),
    Recommended(
        text ="DÀNH CHO BẠN"
    )

}