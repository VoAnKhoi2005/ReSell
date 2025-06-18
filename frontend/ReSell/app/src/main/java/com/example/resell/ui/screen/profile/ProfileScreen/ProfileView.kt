package com.example.resell.ui.screen.profile.ProfileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.resell.ui.theme.LoginButton

@Composable
fun ProfileScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
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
        }

        item {
            // --- Quản lý đơn hàng ---
            SectionTitle(text = "Quản lý đơn hàng")
        }
        item { IconButtonHorizontal("Đơn mua", R.drawable.bag_duotone, modifier = Modifier.padding(vertical = 0.dp), textColor = LoginButton) { } }
        item { IconButtonHorizontal("Đơn bán", R.drawable.desk_duotone,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }

        item {
            // --- Tiện ích ---
            SectionTitle(text = "Tiện ích")
        }
        item { IconButtonHorizontal("Tin đăng đã lưu", R.drawable.favouriteicon,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }
        item { IconButtonHorizontal("Đánh giá từ tôi", R.drawable.rateicon,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }

        item {
            // --- Khác ---
            SectionTitle(text = "Khác")
        }
        item { IconButtonHorizontal("Cài đặt tài khoản", R.drawable.setting_line_duotone,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }
        item { IconButtonHorizontal("Thiết lập vị trí", R.drawable.pin_duotone,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }
        item { IconButtonHorizontal("Đăng xuất", R.drawable.signouticon,modifier = Modifier.padding(vertical = 0.dp),textColor = LoginButton) { } }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color = LightGray, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = GrayFont,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
    }
}
