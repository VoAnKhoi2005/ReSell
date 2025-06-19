package com.example.resell.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.resell.R

@Composable
fun StarRating(
    rating: Float,           // VD: 3.7
    reviewCount: Int? = null, // VD: 120
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Int = 16,
    showText: Boolean = true // Mặc định có hiển thị số sao và số đánh giá
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            val icon: Painter = when {
                index < rating.toInt() -> painterResource(R.drawable.fullstar)
                index < rating -> painterResource(R.drawable.halfstar)
                else -> painterResource(R.drawable.emptystar)
            }

            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(starSize.dp)
            )
        }

        if (showText) {
            Text(
                text = String.format(" %.1f", rating) + (if (reviewCount != null && reviewCount > 0) " ($reviewCount)" else ""),
                modifier = Modifier.padding(start = 4.dp),
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
            )
        }
    }
}
