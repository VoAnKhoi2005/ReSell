
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.navigation.Screen
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
            route = Screen.Main.route
        ) {
            MainLayout(navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController) // màn hình tìm kiếm của bạn
        }





    }
}


