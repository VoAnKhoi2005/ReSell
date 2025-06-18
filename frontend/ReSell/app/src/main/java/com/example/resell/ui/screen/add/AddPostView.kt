package com.example.resell.ui.screen.add

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resell.R
import com.example.resell.ui.components.IconButtonHorizontal
import com.example.resell.ui.theme.DarkBlue
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.LightGray
import com.example.resell.ui.theme.LoginButton

@Composable
fun AddPostScreen(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {}
) {
    // Remember state cho các trường nhập liệu
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedAddress by remember { mutableStateOf("TP. Hồ Chí Minh") }

    val addressOptions = listOf("TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng")

    // Toàn bộ nội dung có thể scroll
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
       RequiredLabel(text = "Danh mục")

        IconButtonHorizontal(
            text = "Xe cộ",
            iconResId = R.drawable.car_category,
            hasBorder = true,
            contentAlignment = Alignment.Start,
            textColor = LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {}

        Spacer(Modifier.height(12.dp))
        RequiredLabel(text = "Chọn ảnh của sản phẩm", showAsterisk = false)
        ImagePickerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            iconResId = R.drawable.add_photo
        )

        Spacer(Modifier.height(12.dp))
        RequiredLabel(text ="Chọn video của sản phẩm", showAsterisk = false)
        ImagePickerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            iconResId = R.drawable.add_video
        )

        Spacer(Modifier.height(12.dp))

        // Các ô nhập liệu
        LabeledTextField(label = "Tiêu đề", value = title, onValueChange = { title = it })
        LabeledTextField(label = "Mô tả", value = description, onValueChange = { description = it }, singleLine = false)
        LabeledDropdown(
            label = "Địa chỉ",
            options = addressOptions,
            selectedOption = selectedAddress,
            onOptionSelected = { selectedAddress = it }
        )
        LabeledTextField(label = "Giá", value = price, onValueChange = { price = it })

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Hủy")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue,
                    contentColor = Color.White
                )
            ) {
                Text("Đăng")
            }
        }

        Spacer(modifier = Modifier.height(12.dp)) // Đệm đáy
    }
}

@Composable
fun ImagePickerBox(
    modifier: Modifier = Modifier,
    iconResId: Int
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(LightGray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = GrayFont,
            modifier = Modifier.size(48.dp)
        )
    }
}
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {

       Row {
           Text(
               label,
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium
           )
           Text(
               text = " *",
               color = Color.Red,
               fontWeight = FontWeight.Bold
           )
       }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LabeledDropdown(
    label: String,
    options: List<String>,
    selectedOption: String = options.first(),
    onOptionSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    val dropdownWidth = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        // Hộp chọn địa chỉ với icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    dropdownWidth.value = coordinates.size.width
                }
                .background(LightGray, shape = RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selected,
                    color = GrayFont,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.dropdown_icon),
                    contentDescription = null,
                    tint = GrayFont
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { dropdownWidth.value.toDp() })
                .background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        selected = option
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
fun RequiredLabel(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    showAsterisk: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        if (showAsterisk) {
            Text(
                text = " *",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

