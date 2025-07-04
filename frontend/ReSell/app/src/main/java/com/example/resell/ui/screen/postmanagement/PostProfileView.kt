import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resell.model.PostStatus
import com.example.resell.ui.components.ProductPostItemHorizontalImageSimple
import com.example.resell.ui.components.ProductPostItemHorizontalImageStatus
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.viewmodel.profile.ProfileDetailViewModel
import com.example.resell.util.getRelativeTime



@Composable
fun ApproveScreen(isCurrentUser: Boolean, viewModel: ProfileDetailViewModel) {
    val posts by viewModel.userApprovedPosts.collectAsState()

    LazyColumn {
        items(posts) { post ->
            ProductPostItemHorizontalImageSimple(
                title = post.title,
                time = getRelativeTime(post.createdAt),
                imageUrl = post.thumbnail,
                price = post.price,
                postStatus = PostStatus.APPROVED,
                onClick = {
                    NavigationController.navController.navigate("productdetail_screen/${post.id}")
                }
            )

        }
    }
}


@Composable
fun NotApprovedScreen(isCurrentUser: Boolean, viewModel: ProfileDetailViewModel) {
    val posts by viewModel.userSoldPosts.collectAsState()

    LazyColumn {
        items(posts) { post ->
            ProductPostItemHorizontalImageSimple(
                title = post.title,
                time = getRelativeTime(post.createdAt),
                imageUrl = post.thumbnail,
                price = post.price,
                postStatus = PostStatus.APPROVED,
                onClick = {
                    NavigationController.navController.navigate("productdetail_screen/${post.id}")
                }
            )

        }
    }
}

