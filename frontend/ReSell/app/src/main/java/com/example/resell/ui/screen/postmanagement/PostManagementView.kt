package com.example.resell.ui.screen.postmanagement

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.UserProfileHeader
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.viewmodel.PostManagementViewModel
import com.example.resell.util.getRelativeTime
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.model.PostStatus
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.store.ReactiveStore.Companion.invoke
import com.example.resell.ui.components.ProductPostItemHorizontalImageStatus
import com.example.resell.ui.components.getPostActions
import com.example.resell.ui.navigation.NavigationController

@Composable
fun PostMangamentScreen(
    viewModel: PostManagementViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    // Collect lists
    val pendingPosts by viewModel.pendingPosts.collectAsState()
    val approvedPosts by viewModel.approvedPosts.collectAsState()
    val rejectedPosts by viewModel.rejectedPosts.collectAsState()
    val soldPosts by viewModel.soldPosts.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getPosts()
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {

        // Tab
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex.value,
            edgePadding = 16.dp
        ) {
            HomeTabs.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    text = {
                        val count = when (tab) {
                            HomeTabs.Pending -> pendingPosts.size
                            HomeTabs.Approved -> approvedPosts.size
                            HomeTabs.NotApproved -> rejectedPosts.size
                            HomeTabs.Sold -> soldPosts.size
                        }
                        Text("${tab.label} ($count)", style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    }
                )
            }
        }

        // Nội dung theo tab
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { pageIndex ->
            when (HomeTabs.entries[pageIndex]) {
                HomeTabs.Pending -> PostList(postList = pendingPosts, PostStatus.PENDING,viewModel)
                HomeTabs.Approved -> PostList(postList = approvedPosts, PostStatus.APPROVED,viewModel)
                HomeTabs.NotApproved -> PostList(postList = rejectedPosts, PostStatus.REJECTED,viewModel)
                HomeTabs.Sold -> PostList(postList = soldPosts, PostStatus.SOLD,viewModel)
            }
        }
    }
}

@Composable
fun PostList(postList: List<PostData>, postStatus: PostStatus, viewModel: PostManagementViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (postList.isEmpty()) {
            // Chỉ phần Text được căn giữa
            Text(
                "Không có bài đăng",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(postList) { post ->
                    ProductPostItemHorizontalImageStatus(
                        title = post.title,
                        time = getRelativeTime(post.createdAt),
                        imageUrl = post.thumbnail,
                        price = post.price,
                        address = post.address,
                        postStatus = postStatus,
                        onClick = {
                            // Điều hướng đến màn hình chi tiết bài đăng
                            NavigationController.navController.navigate("productdetail_screen/${post.id}")
                        },
                        getActions = {
                            getPostActions(
                                postStatus = postStatus,
                                onEdit = { viewModel.onEdit(post.id) },
                                onDelete = { viewModel.onDelete(post.id) }

                            )
                        }
                    )
                }
            }
        }
    }
}

enum class HomeTabs(val label: String) {
    Pending("CHỜ DUYỆT"),
    Approved("ĐANG BÁN"),
    Sold("ĐÃ BÁN"),
    NotApproved("BỊ TỪ CHỐI"),

}
