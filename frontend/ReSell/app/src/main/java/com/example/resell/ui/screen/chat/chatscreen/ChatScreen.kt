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
import android.util.Log


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun ChatScreen() {
    val viewModel: ChatViewModel  = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val messages by viewModel.listMessages.collectAsState()
    val listState = rememberLazyListState()
    val post by viewModel.post.collectAsState()
    val conversation by viewModel.conversation.collectAsState()
    val showSellPopup by viewModel.showSellPopup.collectAsState()
    val sellPopupState by viewModel.sellPopupState.collectAsState()


    var initialScrollDone by remember { mutableStateOf(false) }
    val incomingMessage = viewModel.incomingMessage.collectAsState(initial = null)

    //istyping
    val isTyping = true


    LaunchedEffect(Unit) {
        viewModel.inChat(true)
    }


// Gọi khi rời khỏi màn hình
    DisposableEffect(Unit) {
        onDispose {
            // Gọi hàm suspend từ viewModelScope
            viewModel.viewModelScope.launch {
                viewModel.inChat(false)
            }
        }
    }
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
    LaunchedEffect(incomingMessage.value) {
        incomingMessage.value?.let {
            listState.animateScrollToItem(messages.size)
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


    val displayMessages = remember(messages) { messages.reversed() }
    LaunchedEffect(isTyping) {
        if (isTyping) {
            listState.animateScrollToItem(displayMessages.size + 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                receiverName = viewModel.receiverUsername,
                receiverAvatarUrl = receiverAvatarUrl
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                if (isTyping) {
                    TypingIndicator(receiverAvatarUrl)
                }

                ChatInputBar(
                    viewModel = viewModel,
                    onSendMessage = { text ->
                        coroutineScope.launch {
                            if (viewModel.sendMessage(text))
                                listState.animateScrollToItem(messages.size)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { innerPadding ->
        ChatMessages(
            modifier = Modifier.padding(innerPadding),
            messages = displayMessages,
            receiverAvatarUrl = receiverAvatarUrl,
            post = post,
            listState = listState,
            isTyping = true,
            price = "%,d ₫".format(conversation?.offer?:post?.price),
            viewModel = viewModel

        )
    }
    if (showSellPopup) {
        SellPopupView(
            price = conversation?.offer?:0,
            state = sellPopupState,
            onPriceChange = { newPrice -> viewModel.onChangePrice(newPrice) },
            onSellClick = { newPrice -> viewModel.onConfirmSell(newPrice) },
            onCancelSellClick = { viewModel.onCancelSell() },
            onDismissRequest = { viewModel.hideSellPopup() }
        )
    }

}
@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    receiverAvatarUrl: String,
    post: Post?=null,
    price: String,
    listState: LazyListState,
    isTyping: Boolean,
    viewModel: ChatViewModel
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
                    avatarUrl = post?.images?.get(0)?.url?:"",
                    displayName = post?.title?:"",
                    price = price,
                    chatViewModel = viewModel
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
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "selected_image.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.senddImage(file)
        }
    }
    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val locationMessageKey : String = stringResource(id = R.string.location_message_key)
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
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

                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } catch (e: SecurityException) {
                    viewModel.hideLoading()
                    Toast.makeText(context, "Chưa được cấp quyền vị trí", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(context, "Vui lòng cấp quyền vị trí", Toast.LENGTH_SHORT).show()
            }
        }) {
            Image(
                painter = painterResource(id = R.drawable.send_location),
                contentDescription = "Send",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(DarkBlue)
            )
        }


        IconButton(onClick = {
            pickImageLauncher.launch("image/*")
        }) {
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
    val currentUser by ReactiveStore<User>().item.collectAsState()
    val isCurrentUser = message.senderId == currentUser?.id
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) UserMessage else BuyerMessage
    val locationMessageKey : String = stringResource(id = R.string.location_message_key)
    val imageMessageKey : String = stringResource(id = R.string.image_message_key)
    val systemMessageKey:  String = stringResource(id = R.string.system_message_key)
    val isLocationMessage = message.content.contains(locationMessageKey)
    val isImageMessage = message.content.contains(imageMessageKey)
    val isSystemMessage = message.content.contains(systemMessageKey)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isSystemMessage){
            val messageText = message.content.removePrefix("$systemMessageKey ").trim()
            SystemBubble(messageText)
            return
        }
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
        else if (isImageMessage) {
            val urlStartIndex = message.content.indexOf("http")
            val imageUrl = message.content.substring(urlStartIndex)
            ImageBubble(imageUrl)
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
fun SystemBubble(content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier
                .background(Color(0xFFEDEDED), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
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
@Composable
fun ImageBubble(imageUrl: String) {
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val state = painter.state
    val density = LocalDensity.current

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val maxWidthDp = screenWidth * 0.7f  // ảnh chiếm tối đa 70% chiều rộng màn hình
    val maxHeightDp = screenHeight * 0.4f // ảnh không quá 40% chiều cao màn hình


    // Tính width và height dựa trên kích thước thật và chuyển về dp
    val (imageWidthDp, imageHeightDp) = if (state is AsyncImagePainter.State.Success) {
        val sizePx = painter.intrinsicSize
        if (sizePx.width > 0 && sizePx.height > 0) {
            with(density) {
                val widthDp = sizePx.width.toDp()
                val heightDp = sizePx.height.toDp()

                // Co giãn ảnh theo tỉ lệ nhưng không vượt quá maxWidth và maxHeight
                val scale = minOf(
                    maxWidthDp / widthDp,
                    maxHeightDp / heightDp,
                    1f // không scale lên nếu nhỏ hơn
                )

                Pair(widthDp * scale, heightDp * scale)
            }
        } else {
            Pair(maxWidthDp, maxWidthDp) // fallback vuông
        }
    } else {
        Pair(maxWidthDp, maxWidthDp) // placeholder tạm
    }

    Box(
        modifier = Modifier
            .size(width = imageWidthDp, height = imageHeightDp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        Image(
            painter = painter,
            contentDescription = "Shared image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
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
fun OfferView(
    avatarUrl: String,
    displayName: String,
    price: String,
    chatViewModel: ChatViewModel
) {
    val isSeller by chatViewModel.isSeller.collectAsState()
    val isSold by chatViewModel.isSold.collectAsState()
    val conversation by chatViewModel.conversation.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, LightGray, RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
            Row {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    color = Red,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                val buttonText: String
                val buttonEnabled: Boolean
                val onClickAction: () -> Unit

                if (isSold) {
                    buttonText = "Đã bán"
                    buttonEnabled = false
                    onClickAction = {}
                } else {
                    when {
                        isSeller && !(conversation?.isSelling ?: false) -> {
                            buttonText = "Bán ngay"
                            buttonEnabled = true
                            onClickAction = {
                                Log.d("OfferView", "Gọi showEditPricePopup()")
                                chatViewModel.showEditPricePopup()
                            }
                        }

                        isSeller -> {
                            buttonText = "Chỉnh sửa"
                            buttonEnabled = true
                            onClickAction = {
                                chatViewModel.showConfirmPopup()
                            }
                        }

                        !isSeller && !(conversation?.isSelling ?: false) -> {
                            buttonText = "Chưa bán"
                            buttonEnabled = false
                            onClickAction = {}
                        }

                        else -> { // !isSeller && isSelling
                            buttonText = "Mua ngay"
                            buttonEnabled = true
                            onClickAction = {
                                chatViewModel.onBuyClick()
                            }
                        }
                    }
                }

                Button(
                    onClick = onClickAction,
                    enabled = buttonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (buttonEnabled) GreenButton else Color.Gray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 4.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable//trạng thái người bên kia
fun TypingIndicator(avatarUrl: String) {
    var dotCount by remember { mutableStateOf(0) }

    // Update dot count every 500ms
    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            kotlinx.coroutines.delay(500)
        }
    }

    val dots = ".".repeat(dotCount)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = avatarUrl),
            contentDescription = "Typing avatar",
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Đang nhập$dots",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
@Composable
fun SellPopupView(
    price: Int,
    state: SellPopupState,
    onPriceChange: (Int) -> Unit,
    onSellClick: (Int) -> Unit,
    onCancelSellClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var inputText by remember { mutableStateOf(price.toString()) }
    var showError by remember { mutableStateOf(false) }

    val parsedPrice = inputText.toIntOrNull()
    val isValid = parsedPrice != null && parsedPrice in 0..100_000_000
    showError = inputText.isNotEmpty() && !isValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // TopBar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state == SellPopupState.EditPrice) "Sửa giá sản phẩm" else "Xác nhận bán",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng"
                        )
                    }
                }

                Divider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        label = { Text("Giá sản phẩm (VNĐ)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        isError = showError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showError) {
                        Text(
                            text = "Giá không hợp lệ (tối đa 100 triệu)",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state == SellPopupState.EditPrice) {
                        Button(
                            onClick = {
                                parsedPrice?.let {
                                    if (isValid) {
                                        onPriceChange(it) // ✅ Gọi khi sửa giá
                                        onDismissRequest()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isValid
                        ) {
                            Text("Bán ngay")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onCancelSellClick() // ✅ Gọi khi hủy bán
                                    onDismissRequest()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Huỷ bán")
                            }

                            Button(
                                onClick = {
                                    parsedPrice?.let {
                                        if (isValid) {
                                            onSellClick(it) // ✅ Gọi khi xác nhận bán
                                            onDismissRequest()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = isValid
                            ) {
                                Text("Xác nhận bán")
                            }
                        }
                    }
                }
            }
        }
    }
}



enum class SellPopupState {
    EditPrice,
    ConfirmSelling
}

