package com.example.resell.ui.screen.rating
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import kotlinx.coroutines.launch

@Composable
fun RatingScreen() {
    val pagerState = rememberPagerState(pageCount = { RatingTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            titleText = "Đánh giá đơn hàng",
            showBackButton = true,
            onBackClick = { NavigationController.navController.popBackStack() }
        )

        RatingTabsPager(
            selectedTabIndex = selectedTabIndex.value,
            pagerState = pagerState,
            onTabSelected = { /* Analytics or callback here if needed */ }
        )
    }
}

@Composable
fun RatingTabsPager(
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
            RatingTab.entries.forEachIndexed { index, tab ->
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
                            text = tab.text,
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
            when (RatingTab.entries[pageIndex]) {
                RatingTab.Waiting -> WaitingRatingScreen()
                RatingTab.Completed -> CompletedRatingScreen()
            }
        }
    }
}

enum class RatingTab(val text: String) {
    Waiting("Chờ đánh giá"),
    Completed("Đã đánh giá")
}