package com.example.myapplication.ui.components

import android.R
import android.media.tv.AdRequest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.GrayFont
import com.example.myapplication.ui.theme.LightGray
import com.example.myapplication.ui.theme.priceColor
import java.time.LocalDateTime
import kotlin.time.Duration

@Composable
fun ProductPostItem(
    title: String,
    time: String,
    imageUrl: String, // ảnh đầu tiên
    modifier: Modifier = Modifier,
    price: Int,
    category: String,
    address: String
) {
    Card (
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = LightGray
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Hình ảnh đầu tiên (thumbnail)
            AsyncImage( // từ coil-compose
                model = imageUrl,
                contentDescription = "Product thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tiêu đề bài đăng
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis//quá dài ...
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price.toString(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = priceColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Thời gian đăng
            TimeInfor(time,address)
        }
    }
}
@Composable
fun TimeInfor(label: String, address: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.width(18.dp).height(18.dp),
            tint = LightGray
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
            color = GrayFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "-"+ address,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
            color = GrayFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}




