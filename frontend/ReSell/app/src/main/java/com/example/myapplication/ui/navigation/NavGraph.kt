
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screen.chat.chatscreen.ChatScreen
import com.example.myapplication.ui.screen.login.LoginScreen
import com.example.myapplication.ui.screen.register.RegisterScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route
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
            route = Screen.Chat.route
        ) {
            ChatScreen(navController)
        }
    }
}