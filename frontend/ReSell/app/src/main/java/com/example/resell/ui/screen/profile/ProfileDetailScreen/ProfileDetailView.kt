package com.example.resell.ui.screen.profile.ProfileDetailScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.R
import com.example.resell.ui.components.ProfileHeaderSection
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.screen.postmanagement.ApproveScreen
import com.example.resell.ui.screen.postmanagement.NotApprovedScreen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import kotlinx.coroutines.launch

@Composable
fun ProfileDetailScreen() {
    val pagerState = rememberPagerState(pageCount = { ProfileDetailTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            titleText = "Phạm Thành Long",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        ProfileHeaderSection(
            avatarUrl = "https://i.pinimg.com/736x/b0/d3/8c/b0d38ce8049048d15c70da852021fa82.jpg",
            coverUrl = "https://images.unsplash.com/photo-1560972550-aba3456b5564?w=600&auto=format&fit=crop&q=60",
            name = "Phạm Thành Long",
            rating = "3.5",
            reviewCount = 120,
            userId = "08366333080",
            followerCount = 0,
            followingCount = 0,
            responseRate = "Chưa có thông tin",
            createdAt = "3 tháng",
            address = "Huyện Hòa Thành,Tây Ninh",
            onEditClick = { /* TODO */ },
            onShareClick = { /* TODO */ },
            onChangeCoverClick = { /* TODO */ },
            onChangeAvatarClick = { /* TODO */ },
            showCover = true,
            showShareButton = true,
            showEditButton = true
        )

        ProfileTabsPager(
            selectedTabIndex = selectedTabIndex.value,
            pagerState = pagerState,
            onTabSelected = { /* optional: update ViewModel or other logic */ }
        )
    }
}


enum class ProfileDetailTab(
    val text: String
){
    Approved(
        text ="ĐANG HIỂN THỊ (0)"
    ),
    Sold(
        text ="ĐÃ BÁN (0)"
    )

}
@Composable
fun ProfileTabsPager(
    selectedTabIndex: Int,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileDetailTab.entries.forEachIndexed { index, currentTab ->
                Tab(
                    selected = selectedTabIndex == index,
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    onClick = {
                        onTabSelected(index)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = currentTab.text,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            when (ProfileDetailTab.entries[pageIndex]) {
                ProfileDetailTab.Approved -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ApproveScreen()
                    }
                }
                ProfileDetailTab.Sold -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NotApprovedScreen()
                    }
                }
            }
        }
    }
}
