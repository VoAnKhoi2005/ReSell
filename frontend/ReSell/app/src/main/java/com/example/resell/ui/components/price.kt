package com.example.resell.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.White
import java.text.NumberFormat
import java.util.*

@Composable
fun PriceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            val digits = it.filter { it.isDigit() }
            onValueChange(digits)
        },
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            Text(
                text = "đ",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier
    )
}

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
    // Slider hiển thị theo đơn vị TRIỆU
    var priceRange by remember {
        mutableStateOf((initialMin / 1_000_000f)..(initialMax / 1_000_000f))
    }

    // Hộp nhập theo đơn vị ĐỒNG
    var inputMin by remember { mutableStateOf(initialMin.toString()) }
    var inputMax by remember { mutableStateOf(initialMax.toString()) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lọc theo giá",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight =  FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(text = "Chọn khoảng giá", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                RangeSlider(
                    value = priceRange,
                    onValueChange = {
                        priceRange = it
                        inputMin = (it.start.toInt() * 1_000_000).toString()
                        inputMax = (it.endInclusive.toInt() * 1_000_000).toString()
                    },
                    valueRange = (minPrice / 1_000_000f)..(maxPrice / 1_000_000f),
                    steps = 100,
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
                    Text(text = "${priceRange.start.toInt()} triệu đ", color = Color.Black)
                    Text(text = "${priceRange.endInclusive.toInt()} triệu đ", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PriceTextField(
                    value = inputMin,
                    onValueChange = { inputMin = it },
                    label = "Giá tối thiểu",
                    modifier = Modifier.weight(1f)
                )

                PriceTextField(
                    value = inputMax,
                    onValueChange = { inputMax = it },
                    label = "Giá tối đa",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val min = inputMin.toIntOrNull() ?: 0
                    val max = inputMax.toIntOrNull() ?: 0
                    onApply(min, max)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue,
                    contentColor = White
                )
            ) {
                Text("Áp dụng", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun formatPrice(value: Float): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}tr"
        value >= 1_000 -> "${(value / 1_000).toInt()}K"
        else -> "${value.toInt()}đ"
    }
}

@Composable
fun formatPriceWithD(input: String): String {
    val number = input.toIntOrNull() ?: 0
    val formatted = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(number)
    return buildString {
        append(formatted)
        append(" đ")
    }
}
