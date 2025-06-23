package com.example.resell.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.MainButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    modifier: Modifier = Modifier,
    onActivateSearch: (() -> Unit)? = null
) {
    SearchBar(
        query = "", // Luôn để trống
        onQueryChange = {}, // Không cho nhập
        onSearch = {}, // Không làm gì
        active = false, // Không bật chế độ nhập
        onActiveChange = {
            // Luôn điều hướng khi bấm vào
            onActivateSearch?.invoke()
        },
        placeholder = {
            Text(
                text = "Tìm kiếm...",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = GrayFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MainButton
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onActivateSearch?.invoke() }, // 👈 Tránh active mặc định
        shape = RoundedCornerShape(8.dp),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        )
    ) {
        // Không hiển thị kết quả nào cả
    }
}




@Composable
fun SearchBar1(
    onSearchTextChanged: (String) -> Unit,
    searchText: String
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
            Text("Tìm kiếm trên chợ tốt",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light, fontSize = 14.sp),
            color = GrayFont
        )
                      },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MainButton
            )
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            ,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
    )
}