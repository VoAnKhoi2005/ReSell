package com.example.resell.ui.screen.profile.ProfileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.components.ProfileHeaderSection
import com.example.resell.ui.components.ProfileSimpleHeaderSection
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray

@Composable
fun ProfileScreen()
{
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ProfileSimpleHeaderSection(
            avatarUrl = "https://i.pinimg.com/736x/b0/d3/8c/b0d38ce8049048d15c70da852021fa82.jpg",
            name = "Phạm Thành Long",
            rating = "Chưa có đánh giá",
            userId = "08366333080",
            followerCount = 0,
            followingCount = 0,
            onChangeAvatarClick = {
                NavigationController.navController.navigate(Screen.ProfileDetail.route)
            }
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = LightGray, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                Text(
                    text = "Quản lý đơn hàng",
                    color = GrayFont,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            IconButtonHorizontal("Đơn mua", R.drawable.bag_duotone) { }
            IconButtonHorizontal("Đơn bán", R.drawable.desk_duotone) { }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Tiện ích",
                    color = GrayFont,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            IconButtonHorizontal("Tin đăng đã lưu", R.drawable.favouriteicon) { }
            IconButtonHorizontal("Đánh giá từ tôi", R.drawable.rateicon) { }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Khác",
                    color = GrayFont,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            IconButtonHorizontal("Cài đặt tài khoản", R.drawable.setting_line_duotone) { }
            IconButtonHorizontal("Thiết lập vị trí", R.drawable.pin_duotone) { }
            IconButtonHorizontal("Đăng xuất", R.drawable.signouticon) { }

        }
    }

}