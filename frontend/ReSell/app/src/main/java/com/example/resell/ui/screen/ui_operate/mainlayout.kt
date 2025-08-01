package com.example.resell.ui.screen.ui_operate

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.resell.ui.components.BottomBar
import com.example.resell.ui.components.PhoneVerificationPopup
import com.example.resell.ui.components.ReportPostPopup
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.components.bottomNavItems
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.home.HomeScreen
import com.example.resell.ui.screen.market.MarketScreen
import com.example.resell.ui.screen.postmanagement.PostMangamentScreen
import com.example.resell.ui.screen.profile.ProfileScreen.ProfileScreen
import com.example.resell.ui.viewmodel.NotificationViewModel


@Composable
fun MainLayout(modifier: Modifier = Modifier) {
    val viewModel: MainLayoutViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    val isOpenPopup by viewModel.isOpenPopup.collectAsState()
    val notifications = notificationViewModel.notifications

    // Auto-load notifications
    LaunchedEffect(Unit) {
        notificationViewModel.reloadAll()
    }

    var selectedItem by remember { mutableStateOf(bottomNavItems[0]) }
    val bottomNavController = rememberNavController()
    val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        bottomNavItems.find { it.screen.route == currentRoute }?.let {
            selectedItem = it
        }
    }

    Scaffold(
        topBar = {
            when (currentRoute) {
                Screen.Home.route -> TopBar(
                    showSearch = true,
                    searchQuery = "",
                    onSearchNavigate = {
                        NavigationController.navController.navigate(Screen.Search.route)
                    },
                    showNotificationIcon = true,
                    showEmailIcon = true,
                    notifications = notifications,
                    onMarkAsRead = { notificationViewModel.markAsRead(it) }
                )

                Screen.Manage.route -> TopBar(
                    titleText = "Quản lý tin đăng",
                    showNotificationIcon = true,
                    showEmailIcon = true,
                    notifications = notifications,
                    onMarkAsRead = { notificationViewModel.markAsRead(it) }
                )

                Screen.Market.route -> TopBar(
                    titleText = "Dạo chợ",
                    showNotificationIcon = true,
                    showEmailIcon = true,
                    notifications = notifications,
                    onMarkAsRead = { notificationViewModel.markAsRead(it) }
                )

                Screen.Profile.route -> TopBar(
                    titleText = "Tài khoản",
                    notifications = notifications,
                    onMarkAsRead = { notificationViewModel.markAsRead(it) }
                )
            }
        },
        bottomBar = {
            BottomBar(
                items = bottomNavItems,
                selectedItem = selectedItem,
                onAddClick = { viewModel.onAddClicked() },
                onItemClick = {
                    selectedItem = it
                    bottomNavController.navigate(it.screen.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        DashboardScreen(
            modifier = modifier.padding(innerPadding),
            navController = bottomNavController
        )
    }

    if (isOpenPopup) {
        PhoneVerificationPopup(
            onDismiss = { viewModel.closePopUp() },
            onVerified = { viewModel.onAddPhone(it) }
        )
    }
}

@Composable
fun DashboardScreen(modifier: Modifier, navController: NavHostController)
{


    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Market.route) {
            MarketScreen()
        }

        composable(Screen.Profile.route) { ProfileScreen() }
        composable(Screen.Manage.route) { PostMangamentScreen() }
    }
}



