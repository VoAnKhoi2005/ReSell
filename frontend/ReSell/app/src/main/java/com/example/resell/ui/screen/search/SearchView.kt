package com.example.resell.ui.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.MainButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var active by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        active = true
    }

    val suggestions = listOf("motobiker", "phone", "Laptop", "airconditioner", "Tivi", "fridge")
        .filter { it.contains(query, ignoreCase = true) }

    // 👇 Wrap with Column and align content to top
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { /* TODO */ },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text(
                    text = "Tìm kiếm trên Resell",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp),
                    color = GrayFont
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MainButton,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .focusRequester(focusRequester)
                .focusable()
        ) {
            if (query.isNotBlank()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(suggestions) { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // TODO: Xử lý khi chọn gợi ý
                                }
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "Nhập từ khóa để tìm kiếm...",
                    modifier = Modifier.padding(16.dp),
                    color = MainButton
                )
            }
        }
    }
}
