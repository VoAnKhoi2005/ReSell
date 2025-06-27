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
import com.example.resell.ui.navigation.NavigationController
import com.example.resell.ui.navigation.Screen
import com.example.resell.ui.screen.home.ProductPost
import com.example.resell.ui.theme.GrayFont
import com.example.resell.ui.theme.MainButton
import kotlinx.coroutines.delay



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var active by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        active = true
    }

//lọc theo title bài đăng
    val postList = listOf(
        ProductPost("1", "Xe đạp thể thao", "1 giờ trước", "https://via.placeholder.com/150", 1200000, "Xe cộ", "TP.HCM"),
        ProductPost("2", "iPhone 12", "2 giờ trước", "https://via.placeholder.com/150", 10000000, "Đồ điện tử", "Hà Nội"),
        ProductPost("3", "Tủ lạnh Toshiba", "Hôm qua", "https://via.placeholder.com/150", 3000000, "Tủ lạnh, máy lạnh, máy giặt", "Đà Nẵng"),
        ProductPost("4", "Chó Poodle 2 tháng", "3 ngày trước", "https://via.placeholder.com/150", 2500000, "Thú cưng", "TP.HCM"),
        ProductPost("5", "Máy lạnh LG 2HP", "Hôm nay", "https://via.placeholder.com/150", 5000000, "Tủ lạnh, máy lạnh, máy giặt", "TP.HCM")
    )

    val suggestions = postList
        .map { it.title }
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
            onSearch = {
                if (query.isNotBlank()) {
                    NavigationController.navController.navigate(Screen.ResultSearchScreen.withQuery(query))
                }
            }

            ,
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
                        NavigationController.navController.popBackStack()
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
                                    query = item
                                    NavigationController.navController.navigate(Screen.ResultSearchScreen.withQuery(item))
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
