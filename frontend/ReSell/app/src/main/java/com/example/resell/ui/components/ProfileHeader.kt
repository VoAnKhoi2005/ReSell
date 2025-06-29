package com.example.resell.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import com.example.resell.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.Yellow

@Composable
fun ProfileHeaderSection(//bên detailpropfile
    avatarUrl: String?,
    coverUrl: String?,
    name: String?,
    rating: String?,              // "Chưa có đánh giá"
    reviewCount: Int =0,
    userId: String?,
    followerCount: Int?,
    followingCount: Int?,
    responseRate: String?,        // "Chưa có thông tin"
    createdAt: String?,           // "3 tháng"
    address: String?,             // "Huyện Hòa Thành, Tây Ninh"
    onEditClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onChangeCoverClick: (() -> Unit)? = null,
    onChangeAvatarClick: (() -> Unit)? = null,
    showCover: Boolean = true,
    showEditButton: Boolean = true,
    showShareButton: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (showCover) {
            AsyncImage(
                model = coverUrl,
                contentDescription = "Cover Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            IconButton(
                onClick = { onChangeCoverClick?.invoke() },
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .offset(y = 4.dp)
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
                            .padding(2.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = 16.dp, y = 30.dp)
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            IconButton(
                onClick = { onChangeAvatarClick?.invoke() },
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 4.dp)
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
                            .padding(2.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Buttons cạnh avatar
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = 30.dp, x = -16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(100.dp))

            if (showEditButton && onEditClick != null) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, GrayFont),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Chỉnh sửa thông tin",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showShareButton && onShareClick != null) {
                Button(
                    onClick = onShareClick,
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Text(
                        "Chia sẻ",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(40.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        name?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

            Row {//đánh giá
                StarRating(
                    rating = rating?.toFloatOrNull() ?: 0f,
                    starSize = 18,
                    reviewCount = reviewCount,
                    showText = true
                )


                Spacer(modifier = Modifier.width(40.dp))
            userId?.let {
                Text("ID: $it", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            followerCount?.let {
                Text(it.toString(), style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Người theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("|", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            followingCount?.let {
                Text(it.toString(), style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Đang theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        if (!responseRate.isNullOrBlank()) {
            ChatResponeRate(responseRate)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!createdAt.isNullOrBlank()) {
            CreatedAt(createdAt)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!address.isNullOrBlank()) {
            Address(address)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
@Composable
fun ProfileSimpleHeaderSection(//bên profile
    avatarUrl: String?,
    name: String?,
    rating: String?,              // "Chưa có đánh giá"
    reviewCount: Int=0,
    userName: String?,
    followerCount: Int?,
    followingCount: Int?,
    onChangeAvatarClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable { onChangeAvatarClick?.invoke() } // click toàn vùng
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )


            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color.Gray, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.editprofileicon),
                    contentDescription = "Change avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }


        Spacer(modifier = Modifier.width(16.dp))

        // Thông tin bên phải avatar
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            name?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column {
                StarRating(
                    rating = rating?.toFloatOrNull() ?: 0f,
                    starSize = 18,
                    reviewCount = reviewCount,
                    showText = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                userName?.let {
                    Text(
                        "Username: $it",
                        color = GrayFont,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row {
                followerCount?.let {
                    Text(it.toString(), style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Người theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("|", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                followingCount?.let {
                    Text(it.toString(), style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Đang theo dõi", color = GrayFont, style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp))
                }
            }
        }
    }
}

@Composable
fun ProfileSimpleHeader(
    avatarUrl: String?,
    name: String?,
    rating: Float? = null,
    reviewCount: Int? = null,
    soldCount: Int? = null,
    onChangeAvatarClick: (() -> Unit)? = null,
    showRating: Boolean = true
) {
    Row(
        modifier = Modifier
            .padding(6.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable { onChangeAvatarClick?.invoke() }
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Thông tin người dùng + đánh giá nếu có
        Row(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cột bên trái: tên và đã bán
            Column {
                name?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                if (showRating && rating != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarRating(
                            rating = rating,
                            showText = false,
                            starSize = 18
                        )

                        if (reviewCount != null) {
                            Spacer(modifier = Modifier.width(4.dp)) // Khoảng cách giữa sao và text
                            Text(
                                text = "($reviewCount đánh giá)",
                                style = MaterialTheme.typography.labelSmall.copy(color = GrayFont)
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(6.dp))
                soldCount?.let {
                    Text(
                        "Đã bán $it sản phẩm",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                        color = GrayFont
                    )
                }
            }

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




