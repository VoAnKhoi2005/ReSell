package com.example.resell.ui.screen.Order.MyOrder

import com.example.resell.ui.screen.Order.BuyingOrder.AllOrderScreen
import com.example.resell.ui.screen.Order.BuyingOrder.CancelledOrderScreen
import com.example.resell.ui.screen.Order.BuyingOrder.CompletedOrderScreen
import com.example.resell.ui.screen.Order.BuyingOrder.WaitingConfirmationOrderScreen
import com.example.resell.ui.screen.Order.BuyingOrder.WaitingDeliveryOrderScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import kotlinx.coroutines.launch

@Composable
fun MyOrderScreen() {
    val pagerState = rememberPagerState(pageCount = { BuyingOrderTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            titleText = "Đơn mua",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )
        BuyingOrderTabsPager(
            selectedTabIndex = selectedTabIndex.value,
            pagerState = pagerState,
            onTabSelected = { /* Optional: update state or analytics */ }
        )

    }
}

enum class BuyingOrderTab(val text: String) {
    All("Tất cả"),
    Processing("Đang xử lý"),
    Delivering("Đang giao"),
    Delivred("Đã giao"),
    CancelledRefunded("Hoàn tiền/ Đã hủy")
}
@Composable
fun BuyingOrderTabsPager(
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
            BuyingOrderTab.entries.forEachIndexed { index, tab ->
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
            when (BuyingOrderTab.entries[pageIndex]) {
                BuyingOrderTab.All -> AllOrderScreen()
                BuyingOrderTab.Processing -> ProcessingOrderScreen()
                BuyingOrderTab.CancelledRefunded -> CancelledOrderScreen()
                BuyingOrderTab.Delivering -> DeliveringOrderScreen()
                BuyingOrderTab.Delivred -> DeliveredOrderScreen()
            }
        }
    }
}
