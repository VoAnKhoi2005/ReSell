package com.example.resell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.White
import java.text.NumberFormat
import java.util.*

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
    var priceRange by remember {
        mutableStateOf(initialMin.toFloat()..initialMax.toFloat())
    }

    var inputMin by remember { mutableStateOf((initialMin / 1_000_000).toString()) }
    var inputMax by remember { mutableStateOf((initialMax / 1_000_000).toString()) }

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
                        inputMin = (it.start.toInt() / 1_000_000).toString()
                        inputMax = (it.endInclusive.toInt() / 1_000_000).toString()
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
                    Text(text = "0 triệu", color = Color.Black)
                    Text(text = "100 triệu", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = formatPriceWithD(inputMin),
                    onValueChange = {
                        val digits = it.filter(Char::isDigit)
                        inputMin = digits
                        val min = digits.toIntOrNull()?.coerceIn(
                            minPrice,
                            priceRange.endInclusive.toInt()
                        )
                        if (min != null) {
                            priceRange = min.toFloat()..priceRange.endInclusive
                        }
                    },
                    label = { Text("Giá tối thiểu") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = formatPriceWithD(inputMax),
                    onValueChange = {
                        val digits = it.filter(Char::isDigit)
                        inputMax = digits
                        val max = digits.toIntOrNull()?.times(1_000_000)?.coerceIn(
                            priceRange.start.toInt(),
                            maxPrice
                        )
                        if (max != null) {
                            priceRange = priceRange.start..max.toFloat()
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
                    val min = (inputMin.toIntOrNull() ?: 0) ?:0
                    val max = (inputMax.toIntOrNull() ?: 0) ?:0
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
