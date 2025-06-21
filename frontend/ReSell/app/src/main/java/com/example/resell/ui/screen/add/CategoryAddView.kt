package com.example.resell.ui.screen.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.resell.R
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.model.Category
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.MainButton

@Composable
fun CategorySelectionScreen(
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Xe cộ" to R.drawable.car_category,
        "Đồ điện tử" to R.drawable.electronic_category,
        "Đồ nội thất gia dụng, cây cảnh" to R.drawable.furniture_category,
        "Dịch vụ, du lịch" to R.drawable.service_category,
        "Thú cưng" to R.drawable.pet_category,
        "Tủ lạnh, máy lạnh, máy giặt" to R.drawable.fridge_category,
        "Đồ dùng văn phòng" to R.drawable.offices_category,
        "Thời trang, đồ cá nhân" to R.drawable.clothes_category,
        "Đồ ăn thực phẩm" to R.drawable.food_category,
        "Mẹ và bé" to R.drawable.baby_category,
        "Cho tặng miễn phí" to R.drawable.giveaway_category
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBar(
            titleText = "Đăng tin",
            showBackButton = true,
            onBackClick = { NavigationController.navController.popBackStack() },

        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Chọn danh mục",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(categories) { (title, iconRes) ->
                IconButtonHorizontal(
                    text = title,
                    iconResId = iconRes,
                    hasBorder = true,
                    contentAlignment = Alignment.Start,
                    textColor = LoginButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // TODO: handle click
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
