package com.example.resell.ui.screen.postmanagement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.components.UserProfileHeader
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMangamentScreen() {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {

        UserProfileHeader(
            avatarUrl = "https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            displayName = "Phạm Thành Long",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            HomeTabs.entries.forEachIndexed { index, currentTab ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentTab.ordinal)
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
            when (pageIndex) {
                0 -> {
                    // Nội dung cho "ĐANG HIỂN THỊ"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ApproveScreen()
                    }
                }
                1 -> {
                    // Nội dung cho "BỊ TỪ CHỐI"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NotApprovedScreen()
                    }
                }
                2 -> {
                    // Nội dung cho "CHỜ DUYỆT"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PendingScreen()
                    }
                }
            }
        }

    }
}

enum class HomeTabs(
    val text: String
){
    Approved(
        text ="ĐANG HIỂN THỊ (0)"
    ),
    NotApproved(
     text ="BỊ TỪ CHỐI (0)"
    ),
    Pending(
    text ="CHỜ DUYỆT (0)"
    )
}