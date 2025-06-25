package com.example.resell.ui.screen.chat.chatscreen

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.resell.R
import com.example.resell.ui.components.LoadingDialog
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.*
import com.example.resell.ui.viewmodel.chat.ChatViewModel
import com.example.resell.model.Message
import com.example.resell.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.asPaddingValues
import android.net.Uri
import android.os.Looper


import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.resell.model.Post
import com.example.resell.store.ReactiveStore
import com.example.resell.ui.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun ChatScreen() {
    val viewModel: ChatViewModel  = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val messages by viewModel.listMessages.collectAsState()
    val listState = rememberLazyListState()
    val post by viewModel.post.collectAsState()
    var initialScrollDone by remember { mutableStateOf(false) }
    LoadingDialog(isLoading = isLoading)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
       viewModel.getMessages()

    }
    LaunchedEffect(messages) {
        if (messages.isNotEmpty() && !initialScrollDone) {
            listState.animateScrollToItem(messages.size)
            initialScrollDone = true
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index == 0 && viewModel.isMoreMessage) {
                    val prevIndex = listState.firstVisibleItemIndex
                    val prevOffset = listState.firstVisibleItemScrollOffset

                    // Load thêm và lấy số lượng item được thêm
                    val addedCount = viewModel.loadMoreMessages()

                    // Scroll lại đúng chỗ cũ
                    if (addedCount > 0) {
                        // delay 1 frame để đợi danh sách cập nhật xong
                        withFrameNanos { }
                        viewModel.hideLoading()
                        listState.scrollToItem(
                            index = prevIndex + addedCount-1,
                            scrollOffset = prevOffset
                        )
                    }
                }
            }
    }

    val receiverAvatarUrl =post?.user?.avatarURL?: stringResource(R.string.default_avatar_url)
    val receiverUsername =post?.user?.username?:""

    val displayMessages = remember(messages) { messages.reversed() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                receiverName = receiverUsername,
                receiverAvatarUrl = receiverAvatarUrl
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                ChatInputBar(
                    viewModel = viewModel,
                    onSendMessage = { text -> coroutineScope.launch {
                        if (viewModel.sendMessage(text)) listState.animateScrollToItem(messages.size)
                    }}
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }


    ) { innerPadding ->
        ChatMessages(
            modifier = Modifier.padding(innerPadding),
            messages = displayMessages,
            receiverAvatarUrl,
            post,
            listState
        )


    }
}
@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    receiverAvatarUrl: String,
    post: Post?=null,
    listState: LazyListState
) {

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        stickyHeader {
            Surface(color = Color.White) {
                OfferView(
                    avatarUrl = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a",
                    displayName = post?.title?:"",
                    price = post?.price.toString()
                )
            }

        }
        items(messages) { message ->
            ChatBubble(message = message,receiverAvatarUrl)
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatInputBar(viewModel: ChatViewModel,
    onSendMessage: (String) -> Unit
) {
    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val locationMessageKey : String = stringResource(id = R.string.location_message_key)
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconButton(onClick = {
            locationPermissions.launchMultiplePermissionRequest()
            if (locationPermissions.allPermissionsGranted) {
                    viewModel.showLoading()
                val locationRequest = LocationRequest.create().apply {
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                    fastestInterval = 500
                    numUpdates = 1
                }

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        viewModel.hideLoading()
                        val location = result.lastLocation
                        if (location != null) {
                            val lat = location.latitude
                            val lon = location.longitude
                            val mapUrl = "https://maps.google.com/?q=$lat,$lon"
                            onSendMessage("${locationMessageKey} $mapUrl")
                            fusedLocationClient.removeLocationUpdates(this)
                        } else {
                            Toast.makeText(context, "Không thể lấy vị trí", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                locationPermissions.launchMultiplePermissionRequest()
            }
        }) {
            Image(
                painter = painterResource(id = R.drawable.send_location),
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



        TextField(
            value = msg.value,
            maxLines = 3,
            onValueChange = { msg.value = it },
            modifier = Modifier
                .weight(0.5f)
                .clip(RoundedCornerShape(20.dp)),
            placeholder = {
                Text(text = "Type a message...", style = MaterialTheme.typography.bodyMedium)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = texting,
                unfocusedContainerColor = texting,
                disabledContainerColor = texting,
                errorContainerColor = texting,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal
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
fun ChatBubble(message: Message, receiverAvatarUrl : String) {
    val isCurrentUser = message.senderId == ReactiveStore<User>().item.value?.id
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) UserMessage else BuyerMessage
    val locationMessageKey : String = stringResource(id = R.string.location_message_key)
    val isLocationMessage = message.content.contains(locationMessageKey)

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
                    model = receiverAvatarUrl.takeIf { it.isNotBlank() } ?: "https://static.vecteezy.com/system/resources/previews/009/734/564/original/default-avatar-profile-icon-of-social-media-user-vector.jpg"
                ),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        if (isLocationMessage) {
            val urlStartIndex = message.content.indexOf("https://maps.google.com")
            val mapUrl = message.content.substring(urlStartIndex)
            LocaltionBubble(mapUrl)
        }
        else {
            Box(
                modifier = Modifier
                    .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .widthIn(max = 250.dp)

            ) {

                Text(
                    text = message.content,
                    color = if (isCurrentUser) White2 else Color.Black
                )
            }

        }


    }
}
@Composable
fun LocaltionBubble(locationUrl: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(color = BuyerMessage, shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
            .widthIn(max = 250.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TindangTitle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location), // thay bằng icon của bạn
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Chia sẻ vị trí",
                    fontWeight = FontWeight.Bold,
                    color = LoginButton,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Đây là vị trí của tôi",
                    color = LoginButton,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = TindangTitle,
                contentColor = White2
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(text = "Xem vị trí", style = MaterialTheme.typography.labelMedium)
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
                    style = MaterialTheme.typography.labelMedium,
                    color = White2
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                NavigationController.navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = White2
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
@Composable
fun OfferView( avatarUrl: String,
               displayName: String,
               price: String) {
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
            Row() {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row() {
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    color = Red,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(

                    onClick = {
                        NavigationController.navController.navigate(Screen.Payment.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenButton,
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


