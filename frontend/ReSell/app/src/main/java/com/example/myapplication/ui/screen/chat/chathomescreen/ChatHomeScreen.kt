package com.example.myapplication.ui.screen.chat.chathomescreen

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
import com.example.myapplication.ui.viewmodel.chat.ChatHomeViewState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.LightGray
import com.example.myapplication.ui.viewmodel.chat.ChatHomeViewModel
import model.Conversation
import androidx.compose.foundation.lazy.items
import com.example.myapplication.ui.navigation.NavigationController

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
    Scaffold {
        Box (modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ){
            LazyColumn {
                items(state.conversations){ conversation ->
                    ConversationCard(conversation)
                }
            }
        }
    }
}
@Composable
fun ConversationCard(conversation: Conversation){
    Column {
        Text(text = conversation.sellerId,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, LightGray, RoundedCornerShape(16.dp))
                .clickable{
                   NavigationController.navController.navigate("chat/${conversation.id}")
                }
                .padding(16.dp)

        )
    }
}