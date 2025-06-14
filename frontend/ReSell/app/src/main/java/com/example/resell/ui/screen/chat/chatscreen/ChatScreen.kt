package com.example.resell.ui.screen.chat.chatscreen

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.resell.ui.components.LoadingDialog
import com.example.resell.ui.viewmodel.chat.ChatViewModel
import model.Message
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.example.resell.ui.theme.IconColor
import com.example.resell.ui.theme.SoftBlue
import model.User
import store.DataStore
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.theme.Red
import com.example.myapplication.ui.theme.greenButton
import coil.compose.rememberAsyncImagePainter
import com.example.resell.R
import com.example.resell.ui.theme.BuyerMessage

@Composable
fun ChatScreen(conversationId : String){
    val viewModel : ChatViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoadingDialog(isLoading = state.isLoading)
    LaunchedEffect(key1 = true) {
        viewModel.getMessages(conversationId)
    }
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember { mutableStateOf("") }
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
    Scaffold (topBar = {ChatTopBar("Chúa MHề 4.0")},
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(id = R.drawable.add_icon),
                        tint = DarkBlue,
                        contentDescription = "Send")
                }
                TextField(
                    value = msg.value,
                    maxLines = 3,
                    onValueChange = { msg.value = it },
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 0.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text(text = "Type a message", modifier = Modifier.padding(0.dp)) },
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
                            viewModel.sendMessage(msg.value)
                        }
                        msg.value = ""
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward,
                        tint = DarkBlue,
                        contentDescription = "Send")
                }
            }
        }){ innerPadding ->
        ChatMessages(
            messages = messages,
            modifier = Modifier.padding(innerPadding)
        )
    }




}
@Composable
fun ChatMessages(
    messages: List<Message>,
    modifier: Modifier
) {

    val listState = rememberLazyListState()

    // Auto scroll to bottom on new message
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

        Column(
            modifier = modifier

        ) {
            OfferView(
                avatarUrl = "https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                displayName = "Sản phẩm: Phạm Thành Long sinh viên UIT abcxyzzzzz",
                price = "Miễn phí"

            )

            // LazyColumn chiếm phần còn lại của không gian trong Column
            LazyColumn(
                state = listState// Chiếm hết phần còn lại của Column
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
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
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd
        Box(
            modifier = Modifier
                .padding(8.dp)
                .background(color = SoftBlue, shape = RoundedCornerShape(8.dp))
                .align(alignment)
                .widthIn(min = 0.dp, max = 300.dp)
        ) {
            Text(
                text = message.content, color = IconColor, modifier = Modifier.padding(8.dp)
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
@Composable
fun OfferView( avatarUrl: String,
           displayName: String,
               price: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, LightGray, RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                //NavigationController.navController.navigate("chat/${conversation.id}")
            }
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Row(){
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(){
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    color = Red,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(

                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenButton,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 4.dp)


                ) {
                    Text(
                        text = "Mua ngay",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }



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

