package com.example.myapplication.ui.screen.chat.chatscreen

import android.hardware.lights.Light
import android.transition.Scene
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.ui.components.LoadingDialog
import com.example.myapplication.ui.viewmodel.chat.ChatViewModel
import com.example.myapplication.ui.viewmodel.chat.ChatViewState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.LightGray
import model.Conversation
import java.nio.file.WatchEvent

@Composable
internal fun ChatScreen(navController: NavController){
    val viewModel : ChatViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChatContent(state = state, navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    state: ChatViewState,
    navController: NavController
) {
    LoadingDialog(isLoading = state.isLoading)
    Scaffold {
        Box (modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ){
            LazyColumn {
                items(state.conversations){ conversation ->
                    ConversationCard(conversation, navController)
                }
            }
        }
    }
}
@Composable
fun ConversationCard(conversation: Conversation, navController: NavController){
    Column {
        Text(text = conversation.sellerId,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, LightGray, RoundedCornerShape(16.dp))
                .clickable{
                   // navController.navigate("chat/${conversation.id}")
                    navController.navigate(Screen.Login.route)
                }
                .padding(16.dp)

        )
    }
}