package com.example.resell.ui.components
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.resell.R
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.LoginButton
import com.example.resell.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceFilterBottomSheet(
    minPrice: Float = 0f,
    maxPrice: Float = 100_000_000f,
    initialMin: Float = 0f,
    initialMax: Float = 100_000_000f,
    onDismissRequest: () -> Unit,
    onApply: (Float, Float) -> Unit
) {
    var priceRange by remember { mutableStateOf(initialMin..initialMax) }
    var inputMin by remember { mutableStateOf(initialMin.toInt().toString()) }
    var inputMax by remember { mutableStateOf(initialMax.toInt().toString()) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lọc theo giá",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(text = "Chọn khoảng giá", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                RangeSlider(
                    value = priceRange,
                    onValueChange = {
                        priceRange = it
                        inputMin = it.start.toInt().toString()
                        inputMax = it.endInclusive.toInt().toString()
                    },
                    valueRange = minPrice..maxPrice,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = GrayFont,
                        activeTrackColor = DarkBlue,
                        inactiveTrackColor = LightGray,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )

                )

                // Label min - max ở 2 đầu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${minPrice.toInt()} đ", color = Color.Black)
                    Text(text = "${maxPrice.toInt()} đ", color = Color.Black)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Hai hộp nhập giá
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = inputMin,
                    onValueChange = {
                        inputMin = it.filter { c -> c.isDigit() }
                        val min = inputMin.toFloatOrNull() ?: minPrice
                        priceRange = min..priceRange.endInclusive
                    },
                    label = { Text("Giá tối thiểu") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Text(text = " - ", modifier = Modifier.padding(horizontal = 8.dp))

                OutlinedTextField(
                    value = inputMax,
                    onValueChange = {
                        inputMax = it.filter { c -> c.isDigit() }
                        val max = inputMax.toFloatOrNull() ?: maxPrice
                        priceRange = priceRange.start..max
                    },
                    label = { Text("Giá tối đa") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút áp dụng
            Button(
                onClick = {
                    val min = inputMin.toFloatOrNull() ?: priceRange.start
                    val max = inputMax.toFloatOrNull() ?: priceRange.endInclusive
                    onApply(min, max)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = White)
            ) {
                Text("Áp dụng", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable//hàm hiện giá ở filterbuttong giá
fun formatPrice(value: Float): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}tr"
        value >= 1_000 -> "${(value / 1_000).toInt()}K"
        else -> "${value.toInt()}đ"
    }
}

