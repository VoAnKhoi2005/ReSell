
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.add.AddPostScreen
import com.example.resell.ui.screen.order.BuyingOrder.BuyingOrderScreen
import com.example.resell.ui.screen.order.MyOrder.MyOrderScreen
import com.example.resell.ui.screen.chat.chathomescreen.ChatHomeScreen
import com.example.resell.ui.screen.chat.chatscreen.ChatScreen
import com.example.resell.ui.screen.ui_operate.MainLayout

import com.example.resell.ui.screen.auth.login.LoginScreen
import com.example.resell.ui.screen.auth.phoneAuth.PhoneAuthScreen

import com.example.resell.ui.screen.add.AddScreen
import com.example.resell.ui.screen.add.CategorySelectionScreen
import com.example.resell.ui.screen.address.AddressAddScreen
import com.example.resell.ui.screen.address.AddressSetupScreen
import com.example.resell.ui.screen.address.DistrictSelectScreen
import com.example.resell.ui.screen.address.ProvinceSelectScreen
import com.example.resell.ui.screen.address.WardSelectScreen
import com.example.resell.ui.screen.auth.register.PhoneRegisterScreen

import com.example.resell.ui.screen.productdetail.ProductDetailScreen

import com.example.resell.ui.screen.profile.ProfileDetailScreen.ProfileDetailScreen
import com.example.resell.ui.screen.auth.register.RegisterScreen
import com.example.resell.ui.screen.favourite.FavoriteScreen
import com.example.resell.ui.screen.payment.PaymentScreen
import com.example.resell.ui.screen.rating.RatingScreen
import com.example.resell.ui.screen.rating.ReviewProductScreen

import com.example.resell.ui.screen.search.SearchScreen
import com.example.resell.ui.screen.userinfor.AccountSettingScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Payment.route
    ){
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen()
        }
        composable(
            route = Screen.Register.route+"/{type}/{id}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }
            )
        ) {
            RegisterScreen()
        }
        composable(
            route = Screen.ChatHome.route
        ) {
            ChatHomeScreen()
        }
        composable(
            route = "chat/{conversationId}", // Định nghĩa tuyến đường với đối số
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { ChatScreen()
        }
        composable(
            route = Screen.Main.route
        ) {
            MainLayout()
        }
        composable(Screen.Search.route) {
            SearchScreen() // màn hình tì  m kiếm của bạn
        }
        composable(Screen.ProfileDetail.route+"/{id}",
            arguments = listOf(
                navArgument("id") {type = NavType.StringType  }
            )) {
            ProfileDetailScreen()
        }
        composable(Screen.ProductDetail.route+"/{id}",
            arguments = listOf(
                navArgument("id") {type = NavType.StringType  }
            )) {
            ProductDetailScreen()
        }
        composable(
            route = Screen.PhoneAuth.route + "/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) {
            PhoneAuthScreen()
        }

        composable(Screen.Add.route) { AddScreen() }
        composable (Screen.BuyingOrder.route){ BuyingOrderScreen() }
        composable (Screen.MyOrder.route){ MyOrderScreen() }
        composable (Screen.Payment.route){ PaymentScreen()}
        composable (Screen.AddressSetup.route){ AddressSetupScreen() }
        composable (Screen.AddressAdd.route){ AddressAddScreen()}
        composable (Screen.PhoneRegister.route){ PhoneRegisterScreen() }
        composable(Screen.ProvinceSelect.route) {
            ProvinceSelectScreen { selectedProvince ->
                NavigationController.navController.previousBackStackEntry
                    ?.savedStateHandle?.set("province", selectedProvince)
                NavigationController.navController.popBackStack()
            }
        }

        composable(Screen.DistrictSelect.route) {
            val province = NavigationController.navController
                .currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("province") ?: ""

            DistrictSelectScreen(selectedProvince = province) { selectedDistrict ->
                NavigationController.navController.previousBackStackEntry
                    ?.savedStateHandle?.set("district", selectedDistrict)
                NavigationController.navController.popBackStack()
            }
        }

        composable(Screen.WardSelect.route) {
            val district = NavigationController.navController
                .currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("district") ?: ""

            WardSelectScreen(selectedDistrict = district) { selectedWard ->
                NavigationController.navController.previousBackStackEntry
                    ?.savedStateHandle?.set("ward", selectedWard)
                NavigationController.navController.popBackStack()
            }
        }

        composable(Screen.Favorite.route){
            FavoriteScreen()
        }
        composable(Screen.Rating.route){
            RatingScreen()
        }
        composable(Screen.CategorySelection.route){
            CategorySelectionScreen()
        }
        composable(Screen.AddPost.route){
            AddPostScreen(onCancelClick = {NavigationController.navController.popBackStack()})
        }
        composable(Screen.AccountSetting.route){
           AccountSettingScreen()
        }
        composable(//searchresult
            route = Screen.ResultSearchScreen.route,
            arguments = listOf(navArgument("query") { defaultValue = "" })
        ) {
            val query = it.arguments?.getString("query") ?: ""
            SearchResultScreen(searchQuery = query)
        }

        composable(Screen.ReviewProductScreen.route){
            ReviewProductScreen()
        }


    }
}



