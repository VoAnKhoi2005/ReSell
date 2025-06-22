
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

import com.example.resell.ui.screen.productdetail.ProductDetailScreen
import com.example.resell.ui.screen.productdetail.sampleAddress
import com.example.resell.ui.screen.productdetail.sampleAvatar
import com.example.resell.ui.screen.productdetail.sampleCategory
import com.example.resell.ui.screen.productdetail.sampleDescription
import com.example.resell.ui.screen.productdetail.sampleFollowerCount
import com.example.resell.ui.screen.productdetail.sampleFollowingCount
import com.example.resell.ui.screen.productdetail.sampleImages
import com.example.resell.ui.screen.productdetail.sampleName
import com.example.resell.ui.screen.productdetail.samplePrice
import com.example.resell.ui.screen.productdetail.sampleRating
import com.example.resell.ui.screen.productdetail.sampleTime
import com.example.resell.ui.screen.productdetail.sampleTitle
import com.example.resell.ui.screen.productdetail.sampleUserId
import com.example.resell.ui.screen.profile.ProfileDetailScreen.ProfileDetailScreen
import com.example.resell.ui.screen.auth.register.RegisterScreen
import com.example.resell.ui.screen.favourite.FavoriteScreen
import com.example.resell.ui.screen.payment.PaymentScreen
import com.example.resell.ui.screen.rating.RatingScreen
import com.example.resell.ui.screen.search.SearchScreen
import com.example.resell.ui.screen.userinfor.AccountSettingScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen()
        }
        composable(
            route = Screen.Register.route
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
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId")
            if (conversationId != null) {
                ChatScreen(conversationId)
            } else {
                // Xử lý trường hợp conversationId bị thiếu, ví dụ: hiển thị lỗi hoặc quay lại
                // navController.popBackStack()
                Text("Error: Conversation ID missing")
            }
        }
        composable(
            route = Screen.Main.route
        ) {
            MainLayout()
        }
        composable(Screen.Search.route) {
            SearchScreen(navController) // màn hình tì  m kiếm của bạn
        }
        composable(Screen.ProfileDetail.route) {
            ProfileDetailScreen()
        }
        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(
                images = sampleImages,
                title = sampleTitle,
                price = samplePrice,
                category = sampleCategory,
                time = sampleTime,
                address = sampleAddress,
                description = sampleDescription,
                avatarUrl = sampleAvatar,
                sellerName = sampleName,
                sellerRating = sampleRating,
                sellerId = sampleUserId,
                followerCount = sampleFollowerCount,
                followingCount = sampleFollowingCount,
                onContactClick = { /* TODO */ },
                onBuyClick = { /* TODO */ }
            )
        }
        composable(Screen.PhoneAuth.route) {
            PhoneAuthScreen()
        }
        composable(Screen.Add.route) { AddScreen() }
        composable (Screen.BuyingOrder.route){ BuyingOrderScreen() }
        composable (Screen.MyOrder.route){ MyOrderScreen() }
        composable (Screen.Payment.route){ PaymentScreen()}
        composable (Screen.AddressSetup.route){ AddressSetupScreen() }
        composable (Screen.AddressAdd.route){ AddressAddScreen()}
        /*composable(Screen.ProvinceSelect.route) {
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
*/
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

    }
}


