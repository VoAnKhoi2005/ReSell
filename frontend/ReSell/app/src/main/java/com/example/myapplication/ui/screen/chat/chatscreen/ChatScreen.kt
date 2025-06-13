package com.example.myapplication.ui.screen.chat.chatscreen

import Roboto
import android.R.attr.contentDescription
import android.R.attr.tint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.LoadingDialog
import com.example.myapplication.ui.screen.chat.chathomescreen.ConversationCard
import com.example.myapplication.ui.viewmodel.chat.ChatViewModel
import com.example.myapplication.ui.viewmodel.chat.ChatViewState
import model.Message
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.TopBar
import com.example.myapplication.ui.theme.IconColor
import com.example.myapplication.ui.theme.SoftBlue
import model.User
import store.DataStore
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.navigation.NavigationController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.DarkBlue
import com.example.myapplication.ui.theme.LightGray
import androidx.compose.ui.res.painterResource
@Composable
fun ChatScreen(conversationId : String){
    val viewModel : ChatViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoadingDialog(isLoading = state.isLoading)
    LaunchedEffect(key1 = true) {
        viewModel.getMessages(conversationId)
    }
    DataStore.user = User(
        id = "seller_1",
        username = "seller_one",
        email = "seller1@example.com",
        phone = "0123456789",
        password = "password123", // không nên lưu plain text nếu là bản thật
        fullName = "Nguyễn Văn A",
        citizenId = "123456789012",
        birthday = LocalDate.of(1990, 1, 1),
        gender = true, // true = nam, false = nữ (tùy cách bạn định nghĩa)
        status = "active",
        reputation = 100,
        banStart = null,
        banEnd = null,
        createdAt = LocalDateTime.now()
    )

    val messages = state.messages
    Column(modifier = Modifier.fillMaxSize()) {
        ChatTopBar("Chúa MHề 4.0")
        ChatMessages(
            messages = messages,
            onSendMessage = { text ->
                viewModel.sendMessage(text)
            }
        )
    }

}
@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
) {
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll to bottom on new message
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // chừa khoảng cho TextField
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(30.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.AddCircle,
                    tint = DarkBlue,
                    contentDescription = "Send")
        }
            TextField(
                value = msg.value,
                maxLines = 3,
                onValueChange = { msg.value = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text(text = "Type a message") },
                colors = TextFieldDefaults.colors(
                    // Đã sửa: Sử dụng focusedContainerColor và unfocusedContainerColor
                    focusedContainerColor = Color.LightGray, // Khi TextField được focus
                    unfocusedContainerColor = Color.LightGray, // Khi TextField không được focus
                    disabledContainerColor = Color.LightGray, // Tùy chọn: cho trạng thái bị vô hiệu hóa
                    errorContainerColor = Color.LightGray,   // Tùy chọn: cho trạng thái lỗi

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = IconColor,
                    fontSize = 14.sp,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    hideKeyboardController?.hide()
                })
            )
            IconButton(onClick = {
                if (msg.value.isNotBlank()) {
                    onSendMessage(msg.value)
                    msg.value = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward,
                    tint = DarkBlue,
                    contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == DataStore.user?.id
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)

    ) {
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd
        Box(
            modifier = Modifier
                .padding(8.dp)
                .background(color = SoftBlue, shape = RoundedCornerShape(8.dp))
                .align(alignment)
        ) {
            Text(
                text = message.content, color = IconColor, modifier = Modifier.padding(8.dp)
            )
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Start,
                maxLines = 1,
                modifier = Modifier // Để tiêu đề căn giữa
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
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
            titleContentColor = Color.Black, // Màu chữ của tiêu đề
            navigationIconContentColor = Color.Black // Màu của nút back
        )
    )
}

