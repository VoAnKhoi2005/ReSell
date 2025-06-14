package com.example.resell.ui.screen.chat.chatscreen

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.resell.R
import com.example.resell.ui.components.LoadingDialog
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.*
import com.example.resell.ui.viewmodel.chat.ChatViewModel
import model.Message
import model.User
import store.DataStore
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ChatScreen(conversationId: String) {
    val viewModel: ChatViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoadingDialog(isLoading = state.isLoading)

    LaunchedEffect(key1 = true) {
        viewModel.getMessages(conversationId)
    }

    // Mock user data
    DataStore.user = User(
        id = "seller_1",
        username = "seller_one",
        email = "seller1@example.com",
        phone = "0123456789",
        password = "password123",
        fullName = "Nguyễn Văn A",
        citizenId = "123456789012",
        birthday = LocalDate.of(1990, 1, 1),
        gender = true,
        status = "active",
        reputation = 100,
        banStart = null,
        banEnd = null,
        createdAt = LocalDateTime.now()
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                receiverName = "Chúa MHề 4.0",
                receiverAvatarUrl = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a"
            )
        },
        bottomBar = {
            Column {
                ChatInputBar(onSendMessage = { text -> viewModel.sendMessage(text) })
                Spacer(modifier = Modifier.height(32.dp)) // đẩy lên 8dp
            }
        }

    ) { innerPadding ->
        ChatMessages(
            modifier = Modifier.padding(innerPadding),
            messages = state.messages
        )
    }
}

@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    messages: List<Message>
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        items(messages) { message ->
            ChatBubble(message = message)
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit
) {
    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
            placeholder = {
                Text(text = "Type a message...", style = MaterialTheme.typography.bodyMedium)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BuyerMessage,
                unfocusedContainerColor = BuyerMessage,
                disabledContainerColor = BuyerMessage,
                errorContainerColor = BuyerMessage,
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
                    modifier = Modifier.size(32.dp)
                )
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
                painter = rememberAsyncImagePainter(
                    model = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a"
                ),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    receiverName: String,
    receiverAvatarUrl: String?
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = receiverAvatarUrl ?: "https://default.avatar.url/avatar.png"
                    ),
                    contentDescription = "Receiver Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = receiverName,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SoftBlue,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}
