package com.example.resell.ui.screen.UiOperate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.resell.ui.components.BottomBar
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.components.bottomNavItems
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.home.HomeScreen
import com.example.resell.ui.screen.market.MarketScreen
import com.example.resell.ui.screen.postmanagement.PostMangamentScreen
import com.example.resell.ui.screen.productdetail.ProductDetailScreen
import com.example.resell.ui.screen.profile.ProfileScreen.ProfileScreen


@Composable
fun MainLayout(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf(bottomNavItems[0]) }
    val bottomNavController = rememberNavController()
    // LẤY THÔNG TIN VỀ ROUTE HIỆN TẠI
    val currentBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            when (currentRoute) {
                Screen.Home.route -> TopBar(
                    showSearch = true,
                    showNotificationIcon = true,
                    showEmailIcon = true,
                    onSearchNavigate = {
                        NavigationController.navController.navigate(Screen.Search.route)
                    // đã điều hướng đến controller qua 3 hàm trung gian từ my searchbar dến topbar và đến mainlayout
                    }
                )
                Screen.Manage.route -> TopBar(
                    titleText = "Quản lý tin đăng",
                    showNotificationIcon = true,
                    showEmailIcon = true
                )
                Screen.Market.route -> TopBar(
                    titleText = "Dạo chợ",
                    showNotificationIcon = true,
                    showEmailIcon = true
                )
                Screen.Profile.route -> TopBar(titleText = "Thêm")
                else -> {}
            }
        }
        ,
        bottomBar = {
            BottomBar(
                items = bottomNavItems,
                selectedItem = selectedItem,
                onItemClick = {
                    selectedItem = it
                    bottomNavController.navigate(it.screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(bottomNavController.graph.startDestinationId) {
                            saveState = true
                        }
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



