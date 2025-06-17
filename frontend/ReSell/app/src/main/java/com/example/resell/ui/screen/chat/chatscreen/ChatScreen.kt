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
import androidx.compose.ui.text.style.TextAlign
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
import com.example.resell.ui.viewmodel.chat.ChatViewState
import model.Message
import model.User
import store.DataStore
import java.time.LocalDate
import java.time.LocalDateTime
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import android.content.Intent

import android.net.Uri
import android.os.Looper

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


@Composable
fun ChatScreen(conversationId: String) {
    val viewModel: ChatViewModel  = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoadingDialog(isLoading = state.isLoading)

    LaunchedEffect(key1 = true) {
        viewModel.getMessages(conversationId)
    }
   val receiverAvatarUrl = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a"

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
                receiverAvatarUrl = receiverAvatarUrl.takeIf { it.isNotBlank() } ?: "https://static.vecteezy.com/system/resources/previews/009/734/564/original/default-avatar-profile-icon-of-social-media-user-vector.jpg"
            )
        },
        bottomBar = {
            Column {
                ChatInputBar(viewModel,onSendMessage = { text -> viewModel.sendMessage(text) })
                Spacer(modifier = Modifier.height(32.dp)) // đẩy lên 8dp
            }
        }

    ) { innerPadding ->
        ChatMessages(
            modifier = Modifier.padding(innerPadding),
            messages = state.messages,
            receiverAvatarUrl
        )
    }
}
@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    receiverAvatarUrl: String
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
        stickyHeader {
            Surface(color = Color.White) {
                OfferView(
                    avatarUrl = "https://plus.unsplash.com/premium_photo-1666700698946-fbf7baa0134a",
                    displayName = "iPhone 15 Pro Max 1TB - VN/A",
                    price = "43.000.000₫"
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
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            locationPermissions.launchMultiplePermissionRequest()
            if (locationPermissions.allPermissionsGranted) {
                    viewModel.showLoaading()
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
                            onSendMessage("dab64614f35cbb2e3d8819ef6c1769e4 $mapUrl")

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
fun ChatBubble(message: Message, receiverAvatarUrl : String) {
    val isCurrentUser = message.senderId == DataStore.user?.id
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) SoftBlue else BuyerMessage
    val isLocationMessage = message.content.contains("dab64614f35cbb2e3d8819ef6c1769e4")
    val context = LocalContext.current
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

        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
                .widthIn(max = 250.dp)
                .clickable(enabled = isLocationMessage) {
                    if (isLocationMessage) {
                        val urlStartIndex = message.content.indexOf("https://maps.google.com")
                        val url = message.content.substring(urlStartIndex)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                }
        ) {
            if (isLocationMessage) {
                val urlStartIndex = message.content.indexOf("https://maps.google.com")
                val mapUrl = message.content.substring(urlStartIndex)
                val latLon = mapUrl.substringAfter("?q=")
                val staticMapUrl =
                    "https://maps.googleapis.com/maps/api/staticmap?center=$latLon&zoom=15&size=300x150&markers=color:red%7C$latLon&key=AIzaSyCAloQ8Dt3Fl3TDCJZYALQbWHMg-IvJCwg"//API cần để hiện googlemap

                Column {
                    Image(
                        painter = rememberAsyncImagePainter(staticMapUrl),
                        contentDescription = "Location Thumbnail",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 Vị trí hiện tại của tôi",
                        color = IconColor,
                        fontSize = 13.sp
                    )
                }
            } else {
                Text(
                    text = message.content,
                    color = IconColor
                )
            }

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


