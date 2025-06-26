package com.example.resell.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.White2
import com.example.resell.model.Category

@Composable
fun IconButtonVertical(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String?,
    label: String,
    iconSize: Dp = 30.dp, // nên nhỏ hơn một chút cho đẹp
    fontSize: TextUnit = 12.sp,
    iconTint: Color,
    labelColor: Color = Color.Gray // Thêm màu chữ để bạn dễ tùy biến
) {
    Column(
        modifier = modifier
            .padding(vertical = 6.dp)
            .height(56.dp), // Chiều cao cố định cho đồng đều
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Normal
            ),
            maxLines = 1
        )
    }
}

@Composable
fun IconButtonHorizontal(
    text: String,
    iconResId: Int,
    modifier: Modifier = Modifier,
    hasBorder: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    contentAlignment: Alignment.Horizontal = Alignment.Start,
    textColor: Color = GrayFont,
    iconTint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp),
        border = if (hasBorder) BorderStroke(0.5.dp, LightGray) else null,
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp), // padding nhẹ cho dễ nhìn
            horizontalArrangement = when (contentAlignment) {
                Alignment.CenterHorizontally -> Arrangement.Center
                Alignment.End -> Arrangement.End
                else -> Arrangement.Start
            }
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.run { width(4.dp) })
            Text(text, color = textColor, style = MaterialTheme.typography.labelMedium.copy( fontSize = 16.sp))
        }
    }
}
@Composable
fun CircleIconButton(
    iconResId: Int,
    contentDescription: String,
    iconTint: Color = Color.Black,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(White2)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
    }
}
@Composable
fun IconWithTextRow(iconResId: Int, text: String, iconTint: Color =Gray) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .padding(end = 6.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconTint)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}







