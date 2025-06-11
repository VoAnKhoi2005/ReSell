
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screen.MainLayout
import com.example.myapplication.ui.screen.home.HomeScreen
import com.example.myapplication.ui.screen.login.LoginScreen
import com.example.myapplication.ui.screen.market.MarketScreen
import com.example.myapplication.ui.screen.newsmanagement.PostMangamentScreen
import com.example.myapplication.ui.screen.profile.ProfileScreen
import com.example.myapplication.ui.screen.register.RegisterScreen

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



    }
}


