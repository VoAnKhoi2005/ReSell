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
    minPrice: Int = 0,
    maxPrice: Int = 100_000_000,
    initialMin: Int = 0,
    initialMax: Int = 100_000_000,
    onDismissRequest: () -> Unit,
    onApply: (Int, Int) -> Unit
) {
    // Float range cho RangeSlider
    var priceRange by remember {
        mutableStateOf(initialMin.toFloat()..initialMax.toFloat())
    }

    // TextField input (chuỗi)
    var inputMin by remember { mutableStateOf(initialMin.toString()) }
    var inputMax by remember { mutableStateOf(initialMax.toString()) }

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
                    valueRange = minPrice.toFloat()..maxPrice.toFloat(),
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = GrayFont,
                        activeTrackColor = DarkBlue,
                        inactiveTrackColor = LightGray,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${minPrice} đ", color = Color.Black)
                    Text(text = "${maxPrice} đ", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = inputMin,
                    onValueChange = {
                        inputMin = it.filter(Char::isDigit)
                        val min = inputMin.toFloatOrNull()?.coerceIn(minPrice.toFloat(), priceRange.endInclusive)
                        if (min != null) {
                            priceRange = min..priceRange.endInclusive
                        }
                    },
                    label = { Text("Giá tối thiểu") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Text(text = " - ", modifier = Modifier.padding(horizontal = 8.dp))

                OutlinedTextField(
                    value = inputMax,
                    onValueChange = {
                        inputMax = it.filter(Char::isDigit)
                        val max = inputMax.toFloatOrNull()?.coerceIn(priceRange.start, maxPrice.toFloat())
                        if (max != null) {
                            priceRange = priceRange.start..max
                        }
                    },
                    label = { Text("Giá tối đa") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val min = inputMin.toIntOrNull() ?: priceRange.start.toInt()
                    val max = inputMax.toIntOrNull() ?: priceRange.endInclusive.toInt()
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

