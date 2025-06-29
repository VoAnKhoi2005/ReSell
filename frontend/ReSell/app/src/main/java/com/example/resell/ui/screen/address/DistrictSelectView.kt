package com.example.resell.ui.screen.address
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resell.ui.components.TopBar
import com.example.resell.ui.navigation.NavigationController
import androidx.compose.foundation.lazy.items

@Composable
fun DistrictSelectScreen(
    selectedProvince: String,
    onDistrictSelected: (String) -> Unit
) {
    // Dữ liệu mẫu tuỳ theo selectedProvince
    val districts = when (selectedProvince) {
        "TP.HCM" -> listOf("Quận 1", "Quận 3", "Quận 7", "Bình Thạnh")
        "Hà Nội" -> listOf("Ba Đình", "Hoàn Kiếm", "Cầu Giấy", "Đống Đa")
        else -> listOf("Trung tâm", "Ngoại ô")
    }
    var search by remember { mutableStateOf("") }

    val filtered = districts.filter {
        it.contains(search, ignoreCase = true)
    }

    Column {
        TopBar(
            titleText = "Chọn Quận/Huyện",
            showBackButton = true,
            onBackClick = {
                NavigationController.navController.popBackStack()
            }
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Tìm kiếm") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        LazyColumn {
            items(filtered) { district ->
                Text(
                    text = district,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {



                            NavigationController.navController.popBackStack()
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
