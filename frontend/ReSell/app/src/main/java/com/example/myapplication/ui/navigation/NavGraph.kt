
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screen.chat.chathomescreen.ChatHomeScreen
import com.example.myapplication.ui.screen.chat.chatscreen.ChatScreen
import com.example.myapplication.ui.screen.UiOperate.MainLayout
import com.example.myapplication.ui.screen.login.LoginScreen
import com.example.myapplication.ui.screen.register.RegisterScreen
import com.example.myapplication.ui.screen.search.SearchScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
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
            MainLayout(navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController) // màn hình tìm kiếm của bạn
        }
    }
}


