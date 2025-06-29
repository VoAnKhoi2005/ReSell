package com.example.resell.ui.screen.chat.chathomescreen

import com.example.resell.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.resell.ui.components.LoadingDialog
import com.example.resell.ui.viewmodel.chat.ChatHomeViewState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.viewmodel.chat.ChatHomeViewModel
import com.example.resell.model.Conversation
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.model.User
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2

@Composable
internal fun ChatHomeScreen(){
    val viewModel : ChatHomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatHomeContent(state = state)
    LaunchedEffect(Unit) {
        viewModel.getConversations()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHomeContent(
    state: ChatHomeViewState,
) {

    LoadingDialog(isLoading = state.isLoading)
    Scaffold(
        topBar = { ChatHomeTopBar() }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){

            LazyColumn {
                items(state.conversationCards.sortedByDescending { it.lastUpdatedAt }){ conversationCard ->
                    val isBuyer = conversationCard.buyerId == (ReactiveStore<User>().item.value?.id ?: "")
                    val avt = if (isBuyer) conversationCard.sellerAvatar else conversationCard.buyerAvatar
                    val username = if (isBuyer) conversationCard.sellerFullName else conversationCard.buyerFullName
                    ConversationCard(avt?: stringResource(R.string.default_avatar_url),
                        username,
                        conversationCard.postThumbnail,
                        conversationCard.postTitle?:"Mô tả sản phẩm",
                        conversationCard.conversationId)
                }
            }
        }
    }

}

@Composable
fun ConversationCard(
    avtUrl: String,
    name: String,
    productImg: String,
    productName: String,
    conversationId: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                NavigationController.navController.navigate("chat/${conversationId}")
            }
            .padding(horizontal = 16.dp, vertical = 8.dp) // tăng padding
    ) {
        AsyncImage(
            model = avtUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp) // to hơn
                .clip(CircleShape)
                .border(1.dp, LightGray, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy( // to hơn
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = productName,
                style = MaterialTheme.typography.bodySmall.copy( // to hơn
                    color = GrayFont,
                    fontSize = 13.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        AsyncImage(
            model = productImg,
            contentDescription = "Product",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp) // to hơn
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, LightGray, RoundedCornerShape(10.dp))
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHomeTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Tin Nhắn",
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 20.sp),

            )
        },
        navigationIcon = {
            IconButton(onClick = {
                NavigationController.navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SoftBlue, // Màu nền của TopBar
            titleContentColor = White2, // Màu chữ của tiêu đề
            navigationIconContentColor = White2 // Màu của nút back
        )
    )}

