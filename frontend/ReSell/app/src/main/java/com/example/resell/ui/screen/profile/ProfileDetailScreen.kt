package com.example.resell.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.R
import com.example.resell.ui.screen.postmanagement.ApproveScreen
import com.example.resell.ui.screen.postmanagement.HomeTabs
import com.example.resell.ui.screen.postmanagement.NotApprovedScreen
import com.example.resell.ui.screen.postmanagement.PendingScreen
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import kotlinx.coroutines.launch
import okhttp3.Address

@Composable
fun ProfileScreen() {
    val pagerState = rememberPagerState(pageCount = { ProfileDetailTab.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Tổng chiều cao chứa cover + phần avatar chìa ra
        ) {
            // Ảnh bìa
            AsyncImage(
                model = "https://images.unsplash.com/photo-1560972550-aba3456b5564?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YW5pbWV8ZW58MHx8MHx8fDA%3D",
                contentDescription = "Cover Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            // Nút camera trên ảnh bìa
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .offset(y = 4.dp) // đẩy ra một chút nếu muốn
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Change coverphoto",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp), // tránh icon bị đè
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = 30.dp)
            ) {
                // Avatar hình tròn
                AsyncImage(
                    model = "https://i.pinimg.com/736x/b0/d3/8c/b0d38ce8049048d15c70da852021fa82.jpg",
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 4.dp) // đẩy ra một chút nếu muốn
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.Gray, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "Change avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp), // tránh icon bị đè
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            // Các nút cạnh avatar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = 30.dp, x = -16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(100.dp))


                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, GrayFont),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)

                ) {
                    Text("Chỉnh sửa thông tin",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                        )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp) // padding nhỏ, chữ sát
                ) {
                    Text(
                        "Chia sẻ",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                    )
                }

            }



        }

        Spacer(modifier = Modifier.height(40.dp)) // Đẩy phần dưới xuống để tránh avatar bị che

        // Thông tin người dùng
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Phạm Thành Long", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp ))
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text("Chưa có đánh giá", style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(40.dp))
                Text("ID: 08366333080", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))

            }


            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Row {
                    Text("0", style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Người theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("|", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(4.dp))
                Row {
                    Text("0", style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Đang theo dõi theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                }


            }
            Spacer(modifier = Modifier.height(15.dp))
            ChatResponeRate("Chưa có thông tin")
            Spacer(modifier = Modifier.height(8.dp))
            CreatedAt("3 tháng")
            Spacer(modifier = Modifier.height(8.dp))
            Address("Huyện Hòa Thành,Tây Ninh")
            Spacer(modifier = Modifier.height(10.dp))

            ProfileTabsPager(
                selectedTabIndex = selectedTabIndex.value,
                pagerState = pagerState,
                onTabSelected = { /* optional: update ViewModel or other logic */ }
            )
            }

        }


        }

@Composable
fun ChatResponeRate(
rate: String
){
    Row {
        Image(
            painter = painterResource( id = R.drawable.chatrateicon),
            contentDescription = "ChatRate Icon",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text("Tỷ lệ phản hồi chat: $rate", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp))
    }
}
@Composable
fun CreatedAt(
    time: String
){
    Row {
        Image(
            painter = painterResource( id = R.drawable.calendar_add_duotone),
            contentDescription = "CreatedAt",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text("Đã tham gia: $time", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp))
    }
}
@Composable
fun Address(
    address: String
){
    Row {
        Image(
            painter = painterResource( id = R.drawable.pin_duotone),
            contentDescription = "ChatRate Icon",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text("Địa chỉ: $address", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp))
    }
}
enum class ProfileDetailTab(
    val text: String
){
    Approved(
        text ="ĐANG HIỂN THỊ (0)"
    ),
    Sold(
        text ="ĐÃ BÁN (0)"
    )

}
@Composable
fun ProfileTabsPager(
    selectedTabIndex: Int,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onTabSelected: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileDetailTab.entries.forEachIndexed { index, currentTab ->
                Tab(
                    selected = selectedTabIndex == index,
                    selectedContentColor = DarkBlue,
                    unselectedContentColor = GrayFont,
                    onClick = {
                        onTabSelected(index)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = currentTab.text,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            when (ProfileDetailTab.entries[pageIndex]) {
                ProfileDetailTab.Approved -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ApproveScreen()
                    }
                }
                ProfileDetailTab.Sold -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NotApprovedScreen()
                    }
                }
            }
        }
    }
}
