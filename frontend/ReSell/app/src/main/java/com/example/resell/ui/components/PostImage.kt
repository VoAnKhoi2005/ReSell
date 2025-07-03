package com.example.resell.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.resell.model.PostStatus
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.White
import com.example.resell.ui.theme.White1
import com.example.resell.ui.theme.priceColor
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductPostItem(
    title: String,
    time: String,
    imageUrl: String, // ảnh đầu tiên
    modifier: Modifier = Modifier,
    price: Int,
    category: String,
    address: String,
    onClick: () -> Unit
) {
    Card (
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick() }
            .border(
                width = 0.5.dp,
                color = White1
            ),

        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(modifier = Modifier.padding(4.dp)) {
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
                color = GrayFont,
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
fun TimeInfor(time: String, address: String) {
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
            text = time,
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
@Composable
fun ProductPostItemHorizontalImage(
    title: String,
    time: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    price: Int,
    address: String,
    showExtraInfo: Boolean = true
) {
    // ...

    val priceFormatted = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(price)
    val annotatedPrice = buildAnnotatedString {
        append(priceFormatted)
        append(" ")
        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
        append("đ")
        pop()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, White1),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor =White)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product thumbnail",
                modifier = Modifier
                    .size(width = 120.dp, height = 100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = annotatedPrice,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = priceColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (showExtraInfo) {
                    TimeInfor(time, address)
                }
            }
        }
    }
}




@Composable
fun ProductPostItemHorizontalImageStatus(
    title: String,
    time: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    price: Int,
    address: String,
    postStatus: PostStatus,
    showExtraInfo: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, White1),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product thumbnail",
                    modifier = Modifier
                        .size(width = 120.dp, height = 100.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = price.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = priceColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (showExtraInfo) {
                        TimeInfor(time, address)
                    }
                }

                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Thêm tuỳ chọn")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        getPostActions(postStatus).forEach { (label, action) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    expanded = false
                                    action()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getPostActions(postStatus: PostStatus): List<Pair<String, () -> Unit>> {
    return when (postStatus) {
        PostStatus.PENDING -> listOf(
            "Chỉnh sửa" to { /* TODO */ },
            "Xoá" to { /* TODO */ }
        )
        PostStatus.APPROVED -> listOf(
            "Chỉnh sửa" to { /* TODO */ },
            "Đánh dấu đã bán" to { /* TODO */ },
            "Ẩn bài" to { /* TODO */ }
        )
        PostStatus.SOLD -> listOf(
            "Đăng lại" to { /* TODO */ }
        )
        PostStatus.REJECTED -> listOf(
            "Xem lý do" to { /* TODO */ },
            "Chỉnh sửa lại" to { /* TODO */ }
        )
        PostStatus.DELETED -> listOf(
            "Hiển thị lại" to { /* TODO */ },
            "Xoá" to { /* TODO */ }
        )
    }
}
