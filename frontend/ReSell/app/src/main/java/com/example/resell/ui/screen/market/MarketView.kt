package com.example.resell.ui.screen.market
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.R
import com.example.resell.model.PostData
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProductPostItemHorizontalImage
import com.example.resell.ui.components.ProfileSimpleHeader
import com.example.resell.ui.components.TimeInfor
import com.example.resell.ui.screen.postmanagement.PostList
import com.example.resell.ui.screen.productdetail.ImageCarousel
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.UserMessage
import com.example.resell.ui.theme.White2
import com.example.resell.ui.viewmodel.market.MarketViewModel
import com.example.resell.util.getRelativeTime
import kotlinx.coroutines.launch

@Composable
fun MarketScreen() {
    val viewModel : MarketViewModel = hiltViewModel()
    val listFollow by viewModel.followPosts.collectAsState()
    val listExplore by viewModel.explorePosts.collectAsState()
    val pagerState = rememberPagerState(pageCount = { MarketTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    val scope = rememberCoroutineScope()

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
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { pageIndex ->
            when (MarketTab.entries[pageIndex]) {
                MarketTab.Follow -> FollowScreen(listFollow){viewModel.getMoreFollow()}
                MarketTab.Explore -> ExploreScreen(listExplore){viewModel.getMoreExplore()}
            }
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
    )

}