package com.example.resell.ui.screen.no_result

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.R
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LoginButton

@Composable
fun EmptyStateScreen(
    imageRes: Int,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LoginButton
            )

            Text(
                text = message,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                color = GrayFont
            )
        }
    }
}

@Composable
fun NoResultScreen() {
    EmptyStateScreen(
        imageRes = R.drawable.noresult,
        title = "Không có kết quả mà bạn muốn",
        message = "Hãy kiểm tra lại sau nhé"
    )
}

@Composable
fun NoRatingScreen() {
    EmptyStateScreen(
        imageRes = R.drawable.noresult,
        title = "Bạn chưa có đánh giá nào",
        message = "Hãy mua bán trên Resell và đánh giá để xây dựng cộng đồng chất lượng hơn nhé"
    )
}

@Composable
fun NoFavoriteScreen() {
    EmptyStateScreen(
        imageRes = R.drawable.noresult,
        title = "Bạn chưa có tin đã lưu nào",
        message = "Hãy quay lại sau nhé"
    )
}



