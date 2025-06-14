package com.example.myapplication.ui.screen.chat.chatscreen

import Roboto
import android.R.attr.contentDescription
import android.R.attr.tint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BuyerMessage

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
                .padding(bottom = 42.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = "Send",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(DarkBlue)
                )
            }

            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "SendImage",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(DarkBlue)
                )
            }
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_ondemand_video_24),
                    contentDescription = "SendVideo",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(DarkBlue)
                )
            }

            TextField(
                value = msg.value,
                maxLines = 3,
                onValueChange = { msg.value = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text(text = "Type a message...", style = MaterialTheme.typography.bodyMedium) },
                colors = TextFieldDefaults.colors(
                    // Đã sửa: Sử dụng focusedContainerColor và unfocusedContainerColor
                    focusedContainerColor = BuyerMessage, // Khi TextField được focus
                    unfocusedContainerColor = BuyerMessage, // Khi TextField không được focus
                    disabledContainerColor = BuyerMessage, // Tùy chọn: cho trạng thái bị vô hiệu hóa
                    errorContainerColor = BuyerMessage,   // Tùy chọn: cho trạng thái lỗi

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
            IconButton(
                onClick = {
                    if (msg.value.isNotBlank()) {
                        onSendMessage(msg.value)
                        msg.value = ""
                    }
                },
                enabled = msg.value.isNotBlank()
            ) {
                if (msg.value.isNotBlank()) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_send_24),
                        contentDescription = "Send",
                        colorFilter = ColorFilter.tint(DarkBlue),
                        modifier = Modifier.size(32.dp) // hoặc chỉnh theo nhu cầu
                    )
                }
            }


        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == DataStore.user?.id
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) SoftBlue else BuyerMessage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isCurrentUser) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a?q=80&w=1936&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(text = message.content, color = IconColor)
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = rememberAsyncImagePainter(model ="https://images.unsplash.com/photo-1571757767119-68b8dbed8c97?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" ),
                contentDescription = "Your Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
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
                    .padding(horizontal = 6.dp)
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

