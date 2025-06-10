package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.GrayFont
import com.example.myapplication.ui.theme.MainButton

@Composable
fun SearchTopBarContent(
    onSearchTextChanged: (String) -> Unit,
    searchText: String
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
            Text("Tìm kiếm trên chợ tốt",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light, fontSize = 16.sp),
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
            .padding(end = 8.dp),
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