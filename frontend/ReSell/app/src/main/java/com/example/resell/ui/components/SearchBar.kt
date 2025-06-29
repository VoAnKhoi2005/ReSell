package com.example.resell.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import com.example.resell.R
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.MainButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onClearSearch: () -> Unit,
    onSearchNavigate: (() -> Unit)? = null
) {
    var activeState by remember { mutableStateOf(false) }
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium.copy(
            fontSize = 18.sp,
            color = Color.Black
        )
    ){
        SearchBar(
            query = searchQuery,
            onQueryChange = {}, // không cho nhập
            onSearch = {},
            active = activeState,
            onActiveChange = { isActive ->
                if (isActive && searchQuery.isBlank()) {
                    onSearchNavigate?.invoke()
                }
                activeState = false // tắt lại luôn
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
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Xóa tìm kiếm",
                        tint = Color.Gray,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { onClearSearch() }
                            .size(20.dp)
                    )
                }
            },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = SearchBarDefaults.colors(containerColor = Color.White)
        ) {}
    }

}

