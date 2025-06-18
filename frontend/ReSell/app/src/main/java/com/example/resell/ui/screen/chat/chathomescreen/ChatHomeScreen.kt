package com.example.resell.ui.screen.chat.chathomescreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import model.Conversation
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.SoftBlue
import com.example.resell.ui.theme.White2

@Composable
internal fun ChatHomeScreen(){
    val viewModel : ChatHomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatHomeContent(state = state)
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
                items(state.conversations){ conversation ->
                    ConversationCard("https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        "Chúa Hề 4.0",
                        "https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        "Sản phẩm: Phạm Thành Long sinh viên UIT abcxyzzzzz",
                        conversation.id)
                }
            }
        }
    }

}

@Composable
fun ConversationCard(avtUrl :String,name: String,productImg:String, productName: String, conversationId: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, LightGray, RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                NavigationController.navController.navigate("chat/${conversationId}")
            }
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        AsyncImage(
            model = avtUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(end = 12.dp).weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = productName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        AsyncImage(
            model = productImg,
            contentDescription = "product",
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))

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

